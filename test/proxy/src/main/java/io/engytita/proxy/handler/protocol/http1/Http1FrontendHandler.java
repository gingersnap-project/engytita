package io.engytita.proxy.handler.protocol.http1;

import static io.engytita.proxy.http.HttpUtil.HTTPS_PORT;
import static io.engytita.proxy.http.HttpUtil.HTTP_PORT;
import static io.engytita.proxy.util.LogWrappers.description;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.engytita.proxy.Address;
import io.engytita.proxy.ConnectionContext;
import io.engytita.proxy.Protocols;
import io.engytita.proxy.ProxyMaster;
import io.engytita.proxy.enums.ProxyMode;
import io.engytita.proxy.event.OutboundChannelClosedEvent;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;

public class Http1FrontendHandler extends ChannelDuplexHandler {

   private static final Logger LOGGER = LoggerFactory.getLogger(Http1FrontendHandler.class);

   private ProxyMaster master;
   private ConnectionContext connectionContext;
   private boolean tunneled;

   private List<ChannelHandler> addedHandlers = new ArrayList<>(3);

   public Http1FrontendHandler(ProxyMaster master, ConnectionContext connectionContext) {
      this.master = master;
      this.connectionContext = connectionContext;
      this.tunneled = connectionContext.connected();
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      LOGGER.debug("{} : handlerAdded", connectionContext);

      addedHandlers.add(new HttpServerCodec());
      addedHandlers.add(new HttpObjectAggregator(master.config().getMaxContentLength()));
      addedHandlers.add(connectionContext.provider().http1EventHandler());
      addedHandlers.forEach(handler -> ctx.pipeline().addBefore(ctx.name(), null, handler));

      if (tunneled) {
         ctx.pipeline().addAfter(ctx.name(), null, connectionContext.provider().wsFrontendHandler());
      }
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) {
      LOGGER.debug("{} : handlerRemoved", connectionContext);
      addedHandlers.forEach(handler -> ctx.pipeline().remove(handler));
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      super.channelInactive(ctx);
      if (connectionContext.connected()) {
         connectionContext.serverChannel().close();
      }
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) {
      if (!(msg instanceof FullHttpRequest)) {
         ctx.fireChannelRead(msg);
         return;
      }
      FullHttpRequest request = (FullHttpRequest) msg;
      if (master.config().getProxyMode() == ProxyMode.HTTP && !tunneled) {
         if (request.method() == HttpMethod.CONNECT) {
            handleTunnelProxyConnection(ctx, request);
         } else {
            handleHttpProxyConnection(ctx, request);
         }
      } else if (master.config().getProxyMode() == ProxyMode.TRANSPARENT && !connectionContext.connected()) {
         handleTransparentProxyConnection(ctx, request);
      } else {
         ctx.fireChannelRead(request);
      }
   }

   @Override
   public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
      super.userEventTriggered(ctx, evt);
      if (evt instanceof OutboundChannelClosedEvent) {
         if (tunneled) {
            ctx.close();
         }
      }
   }

   private void handleTunnelProxyConnection(ChannelHandlerContext ctx, FullHttpRequest request) {
      try {
         Address address = Address.resolve(request.uri(), HTTPS_PORT);
         connectionContext.connect(address, ctx).addListener((future) -> {
            if (!future.isSuccess()) {
               ctx.close();
            }
         });
         FullHttpResponse response =
               new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK);
         LOGGER.debug("{} : {}", connectionContext, description(response));
         ctx.writeAndFlush(response);
         ctx.pipeline().replace(Http1FrontendHandler.this, null,
               connectionContext.provider().tlsFrontendHandler());
      } finally {
         request.release();
      }
   }

   private void handleHttpProxyConnection(ChannelHandlerContext ctx, FullHttpRequest request) {
      //HttpUrl httpUrl = HttpUrl.resolve(request.uri());
      //Address address = new Address(httpUrl.getHost(), httpUrl.getPort());
      //request.setUri(httpUrl.getPath());
      Address address = new Address(connectionContext.config().getRemoteHost(), connectionContext.config().getRemotePort());
      connectionContext.connect(address, ctx).addListener((ChannelFuture future) -> {
         if (future.isSuccess()) {
            LOGGER.debug("{} : {}", connectionContext, description(request));
            ctx.fireChannelRead(request);
         } else {
            request.release();
            ctx.channel().close();
         }
      });
      if (!connectionContext.tlsCtx().isNegotiated()) {
         connectionContext.tlsCtx().disableTls();
         connectionContext.tlsCtx().protocolPromise().setSuccess(Protocols.HTTP_1);
      }
   }

   private void handleTransparentProxyConnection(ChannelHandlerContext ctx, FullHttpRequest request) {
      Address address = Address.resolve(request.headers().get(HttpHeaderNames.HOST), HTTP_PORT);
      connectionContext.connect(address, ctx).addListener((ChannelFuture future) -> {
         if (future.isSuccess()) {
            LOGGER.debug("{} : {}", connectionContext, description(request));
            future.channel().writeAndFlush(request);
         } else {
            request.release();
            ctx.channel().close();
         }
      });
      connectionContext.tlsCtx().disableTls();
      connectionContext.tlsCtx().protocolPromise().setSuccess(Protocols.HTTP_1);
   }
}

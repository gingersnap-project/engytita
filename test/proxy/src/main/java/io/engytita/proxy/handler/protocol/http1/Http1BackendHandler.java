package io.engytita.proxy.handler.protocol.http1;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.engytita.proxy.ConnectionContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class Http1BackendHandler extends ChannelInboundHandlerAdapter {

   private static final Logger LOGGER = LoggerFactory.getLogger(Http1BackendHandler.class);

   private ConnectionContext connectionContext;

   public Http1BackendHandler(ConnectionContext connectionContext) {
      this.connectionContext = connectionContext;
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      LOGGER.debug("{} : handlerAdded", connectionContext);
      ctx.pipeline().addBefore(ctx.name(), null, new HttpClientCodec());
      ctx.pipeline().addBefore(ctx.name(), null, new HttpObjectAggregator(connectionContext.config().getMaxContentLength()));
      ctx.pipeline().addAfter(ctx.name(), null, connectionContext.provider().wsBackendHandler());
   }
}

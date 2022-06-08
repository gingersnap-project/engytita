package io.engytita.proxy.handler.protocol.ws;

import io.engytita.proxy.ConnectionContext;
import io.engytita.proxy.listener.ProxyListener;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class WebSocketEventHandler extends ChannelDuplexHandler {

   private ProxyListener listener;
   private ConnectionContext connectionContext;

   /**
    * Create new instance of web socket event handler.
    *
    * @param connectionContext the connection context
    */
   public WebSocketEventHandler(ConnectionContext connectionContext) {
      this.connectionContext = connectionContext;
      this.listener = connectionContext.listener();
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
         throws Exception {
      if (msg instanceof WebSocketFrame) {
         listener.onWsResponse(connectionContext, (WebSocketFrame) msg);
      }
      ctx.write(msg, promise);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) {
      if (msg instanceof WebSocketFrame) {
         listener.onWsRequest(connectionContext, (WebSocketFrame) msg);
      }
      ctx.fireChannelRead(msg);
   }

   @Override
   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      listener.close(connectionContext);
   }
}

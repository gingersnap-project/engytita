package io.engytita.proxy.handler;

import static java.lang.System.currentTimeMillis;

import io.engytita.proxy.ConnectionContext;
import io.engytita.proxy.event.ForwardEvent;
import io.engytita.proxy.listener.ProxyListener;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class ForwardEventHandler extends ChannelDuplexHandler {

   private ProxyListener listener;
   private ConnectionContext connectionContext;

   private long requestTime;
   private long requestBytes;

   /**
    * Create new instance of http1 event handler.
    *
    * @param connectionContext the connection context
    */
   public ForwardEventHandler(ConnectionContext connectionContext) {
      this.connectionContext = connectionContext;
      this.listener = connectionContext.listener();
   }

   @Override
   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
         throws Exception {
      ByteBuf byteBuf = (ByteBuf) msg;
      listener.onForwardResponse(connectionContext, byteBuf);
      long responseTime = currentTimeMillis();
      ForwardEvent forwardEvent = ForwardEvent.builder(connectionContext)
            .requestBodySize(requestBytes)
            .requestTime(requestTime)
            .responseTime(responseTime)
            .responseBodySize(byteBuf.readableBytes())
            .build();
      try {
         listener.onForwardEvent(connectionContext, forwardEvent);
      } finally {
         requestTime = 0;
      }
      super.write(ctx, msg, promise);
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      ByteBuf byteBuf = (ByteBuf) msg;
      listener.onForwardRequest(connectionContext, byteBuf);
      requestBytes = byteBuf.readableBytes();
      requestTime = currentTimeMillis();
      super.channelRead(ctx, msg);
   }
}

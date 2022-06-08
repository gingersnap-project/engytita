package io.engytita.proxy.handler;

import io.engytita.proxy.ConnectionContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TailBackendHandler extends ChannelInboundHandlerAdapter {

   private ConnectionContext connectionContext;

   public TailBackendHandler(ConnectionContext connectionContext) {
      this.connectionContext = connectionContext;
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) {
      connectionContext.clientChannel().writeAndFlush(msg);
   }
}

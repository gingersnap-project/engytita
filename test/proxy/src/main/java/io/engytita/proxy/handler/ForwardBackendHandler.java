package io.engytita.proxy.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.engytita.proxy.ConnectionContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ForwardBackendHandler extends SimpleChannelInboundHandler<ByteBuf> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ForwardBackendHandler.class);

   private ConnectionContext connectionContext;

   public ForwardBackendHandler(ConnectionContext connectionContext) {
      super();
      this.connectionContext = connectionContext;
   }

   @Override
   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      LOGGER.debug("{} : handlerAdded", connectionContext);
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      LOGGER.debug("{} : channelInactive", connectionContext);
      connectionContext.clientChannel().close();
      ctx.fireChannelInactive();
   }

   @Override
   protected void channelRead0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
      LOGGER.debug("{} : {}", connectionContext, byteBuf);
      connectionContext.clientChannel().writeAndFlush(byteBuf.retain());
   }
}

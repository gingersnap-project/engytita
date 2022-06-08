package io.engytita.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Proxy {
   private static final Logger LOGGER = LoggerFactory.getLogger(Proxy.class);

   private final ProxyConfig config;

   private NioEventLoopGroup bossGroup;
   private NioEventLoopGroup workerGroup;
   private ProxyStatus status = ProxyStatus.NOTCONFIGURED;

   public Proxy(ProxyConfig config) {
      this.config = config;
   }

   public void start() throws Exception {
      bossGroup = new NioEventLoopGroup(1);
      workerGroup = new NioEventLoopGroup();
      try {
         ServerBootstrap bootstrap = new ServerBootstrap()
               .group(bossGroup, workerGroup)
               .channel(NioServerSocketChannel.class)
               .childHandler(new ProxyInitializer(config));
         Channel channel = bootstrap
               .bind(config.getHost(), config.getPort())
               .sync()
               .channel();

         LOGGER.info("proxy is listening at {}:{}",
               config.getHost(), config.getPort());

         status = ProxyStatus.STARTED;

         if (config.getStatusListener() != null) {
            config.getStatusListener().onStart();
         }

         channel.closeFuture().sync();
      } finally {
         bossGroup.shutdownGracefully();
         workerGroup.shutdownGracefully();
         status = ProxyStatus.STOPPED;
      }
   }

   public void stop() {
      if (bossGroup != null) {
         bossGroup.shutdownGracefully();
      }
      if (workerGroup != null) {
         workerGroup.shutdownGracefully();
      }

      status = ProxyStatus.STOPPED;

      if (config.getStatusListener() != null) {
         config.getStatusListener().onStop();
      }
   }
}
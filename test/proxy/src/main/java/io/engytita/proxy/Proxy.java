package io.engytita.proxy;

import org.jboss.logging.Logger;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.incubator.channel.uring.IOUringEventLoopGroup;
import io.netty.incubator.channel.uring.IOUringServerSocketChannel;

public class Proxy {
   private static final Logger LOGGER = Logger.getLogger(Proxy.class);

   private final ProxyConfig config;

   private EventLoopGroup bossGroup;
   private EventLoopGroup workerGroup;
   private ProxyStatus status = ProxyStatus.NOTCONFIGURED;

   public Proxy(ProxyConfig config) {
      this.config = config;
   }

   private EventLoopGroup createEventLoopGroup(int nThreads) {
      return switch (config.getProxyTransport()) {
         case NIO -> new NioEventLoopGroup(nThreads);
         case EPOLL -> new EpollEventLoopGroup(nThreads);
         case URING -> new IOUringEventLoopGroup(nThreads);
      };
   }

   private Class<? extends ServerSocketChannel> getServerChannelClass() {
      return switch (config.getProxyTransport()) {
         case NIO -> NioServerSocketChannel.class;
         case EPOLL -> EpollServerSocketChannel.class;
         case URING -> IOUringServerSocketChannel.class;
      };
   }

   public void start() throws Exception {
      bossGroup = createEventLoopGroup(1);
      workerGroup = createEventLoopGroup(0);
      try {
         ServerBootstrap bootstrap = new ServerBootstrap()
               .group(bossGroup, workerGroup)
               .channel(getServerChannelClass())
               .childHandler(new ProxyInitializer(config))
               /*.childOption(ChannelOption.SO_KEEPALIVE, true)
               .childOption(ChannelOption.TCP_NODELAY, true)*/;
         Channel channel = bootstrap
               .bind(config.getHost(), config.getPort())
               .sync()
               .channel();

         LOGGER.infof("proxy at %s:%d -> %s:%d using %s transport", config.getHost(), config.getPort(), config.getRemoteHost(), config.getRemotePort(), config.getProxyTransport());

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
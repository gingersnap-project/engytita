package io.engytita.proxy;

import io.engytita.proxy.channel.BackendChannelBootstrap;
import io.engytita.proxy.listener.ProxyListenerManagerProvider;
import io.engytita.proxy.listener.ProxyListenerProvider;
import io.engytita.proxy.tls.CertManager;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

public class ProxyMaster {

   private ProxyConfig config;
   private BackendChannelBootstrap backendChannelBootstrap;
   private ProxyListenerManagerProvider listenerProvider;
   private CertManager certManager;

   public ProxyMaster(ProxyConfig config,
                      BackendChannelBootstrap backendChannelBootstrap) {
      this.config = config;
      this.backendChannelBootstrap = backendChannelBootstrap;
      this.listenerProvider = new ProxyListenerManagerProvider(config.getListeners());
      this.certManager = new CertManager(config);
   }

   public ProxyConfig config() {
      return config;
   }

   public HandlerProvider provider(ConnectionContext context) {
      return new HandlerProvider(this, context);
   }

   public ProxyListenerProvider listenerProvider() {
      return listenerProvider;
   }

   public CertManager certManager() {
      return certManager;
   }

   public ChannelFuture connect(ChannelHandlerContext fromCtx, ConnectionContext connectionContext,
                                ChannelHandler handler) {
      return backendChannelBootstrap.connect(fromCtx, connectionContext, handler);
   }
}

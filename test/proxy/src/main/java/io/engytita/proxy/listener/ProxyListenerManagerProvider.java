package io.engytita.proxy.listener;

public class ProxyListenerManagerProvider implements ProxyListenerProvider {

   private final ProxyListeners listenerStore;

   public ProxyListenerManagerProvider(ProxyListeners listenerStore) {
      this.listenerStore = listenerStore;
   }

   @Override
   public ProxyListener create() {
      return new ProxyListenerManager(listenerStore.getListeners().stream()
            .map(ProxyListenerProvider::create)
            .toList());
   }

   @Override
   public Class<? extends ProxyListener> listenerClass() {
      return ProxyListenerManager.class;
   }
}

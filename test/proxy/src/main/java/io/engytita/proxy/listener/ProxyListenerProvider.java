package io.engytita.proxy.listener;

import java.util.function.Predicate;

import io.engytita.proxy.listener.ProxyListener.Empty;

public interface ProxyListenerProvider {
   ProxyListenerProvider EMPTY = new Singleton(new Empty());

   static ProxyListenerProvider empty() {
      return EMPTY;
   }

   static ProxyListenerProvider singleton(ProxyListener listener) {
      return new Singleton(listener);
   }

   static Predicate<ProxyListenerProvider> match(Class<?> listenerClass) {
      if (ProxyListener.class.isAssignableFrom(listenerClass)) {
         return new MatchListenerPredicate(listenerClass);
      }
      return new MatchProviderPredicate(listenerClass);
   }

   ProxyListener create();

   Class<? extends ProxyListener> listenerClass();

   class Singleton implements ProxyListenerProvider {
      private ProxyListener listener;

      public Singleton(ProxyListener listener) {
         this.listener = listener;
      }

      @Override
      public ProxyListener create() {
         return listener;
      }

      @Override
      public Class<? extends ProxyListener> listenerClass() {
         return listener.getClass();
      }

      @Override
      public int hashCode() {
         return listener.hashCode();
      }

      @Override
      public boolean equals(Object o) {
         if (this == o) {
            return true;
         }
         if (o == null || getClass() != o.getClass()) {
            return false;
         }

         Singleton singleton = (Singleton) o;

         return listener.equals(singleton.listener);
      }

      @Override
      public String toString() {
         return "singleton(" + listener + ")";
      }
   }

   class MatchProviderPredicate implements Predicate<ProxyListenerProvider> {
      private Class<?> providerClass;

      public MatchProviderPredicate(Class<?> providerClass) {
         this.providerClass = providerClass;
      }

      @Override
      public boolean test(ProxyListenerProvider listener) {
         return providerClass.equals(listener.getClass());
      }

      @Override
      public String toString() {
         return "Predicate[providerClass=" + providerClass + "]";
      }
   }

   class MatchListenerPredicate implements Predicate<ProxyListenerProvider> {
      private Class<?> listenerClass;

      public MatchListenerPredicate(Class<?> listenerClass) {
         this.listenerClass = listenerClass;
      }

      @Override
      public boolean test(ProxyListenerProvider listener) {
         return listenerClass.equals(listener.listenerClass());
      }

      @Override
      public String toString() {
         return "Predicate[listenerClass=" + listenerClass + "]";
      }
   }
}

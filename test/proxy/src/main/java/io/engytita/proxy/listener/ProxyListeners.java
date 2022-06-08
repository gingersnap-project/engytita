package io.engytita.proxy.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import io.engytita.proxy.exception.ProxyException;

/**
 * This class provides more user-friendly api to configure the listeners.
 */
public class ProxyListeners {

   private List<ProxyListenerProvider> listeners;

   public ProxyListeners() {
      this(new ArrayList<>());
   }

   public ProxyListeners(
         List<ProxyListenerProvider> listeners) {
      this.listeners = listeners;
   }

   /**
    * Add the listener to the first place of the store.
    *
    * @param listener the listener
    * @return the store itself
    */
   public ProxyListeners addFirst(ProxyListener listener) {
      return addFirst(ProxyListenerProvider.singleton(listener));
   }

   /**
    * Add the provider to the first place of the store.
    *
    * @param provider the listener
    * @return the store itself
    */
   public ProxyListeners addFirst(ProxyListenerProvider provider) {
      listeners.add(0, provider);
      return this;
   }

   /**
    * Add the listener to the place after the {@code target}.
    *
    * @param target   the target class, can be a subclass of {@link ProxyListener} or {@link ProxyListenerProvider}
    * @param listener the listener
    * @return the store itself
    */
   public ProxyListeners addAfter(Class<?> target, ProxyListener listener) {
      return addAfter(target, ProxyListenerProvider.singleton(listener));
   }

   /**
    * Add the provider to the place after the {@code target}.
    *
    * @param target   the target class, can be a subclass of {@link ProxyListener} or {@link ProxyListenerProvider}
    * @param provider the listener provider
    * @return the store itself
    */
   public ProxyListeners addAfter(Class<?> target, ProxyListenerProvider provider) {
      return addAfter(ProxyListenerProvider.match(target), provider);
   }

   /**
    * Add the listener to the place after the place that {@code predicate} was matched.
    *
    * @param predicate the predicate to determine the place for the listener to be inserted
    * @param listener  the listener
    * @return the store itself
    */
   public ProxyListeners addAfter(Predicate<ProxyListenerProvider> predicate, ProxyListener listener) {
      return addAfter(predicate, ProxyListenerProvider.singleton(listener));
   }

   /**
    * Add the provider to the place after the place that {@code predicate} was matched.
    *
    * @param predicate the predicate to determine the place for the listener to be inserted
    * @param provider  the provider
    * @return the store itself
    */
   public ProxyListeners addAfter(Predicate<ProxyListenerProvider> predicate,
                                  ProxyListenerProvider provider) {
      int matched = IntStream.range(0, listeners.size())
            .filter(index -> predicate.test(listeners.get(index)))
            .findFirst()
            .orElseThrow(ProxyException.toThrow("Listener not exist in store: %s", predicate));
      listeners.add(matched + 1, provider);
      return this;
   }

   /**
    * Add the listener to the place before the {@code target}.
    *
    * @param target   the target class, can be a subclass of {@link ProxyListener} or {@link ProxyListenerProvider}
    * @param listener the listener
    * @return the store itself
    */
   public ProxyListeners addBefore(Class<?> target, ProxyListener listener) {
      return addBefore(target, ProxyListenerProvider.singleton(listener));
   }

   /**
    * Add the provider to the place before the {@code target}.
    *
    * @param target   the target class, can be a subclass of {@link ProxyListener} or {@link ProxyListenerProvider}
    * @param provider the provider
    * @return the store itself
    */
   public ProxyListeners addBefore(Class<?> target, ProxyListenerProvider provider) {
      return addBefore(ProxyListenerProvider.match(target), provider);
   }

   /**
    * Add the listener to the place before the place that {@code predicate} was matched.
    *
    * @param predicate the predicate to determine the place for the listener to be inserted
    * @param listener  the listener
    * @return the store itself
    */
   public ProxyListeners addBefore(Predicate<ProxyListenerProvider> predicate,
                                   ProxyListener listener) {
      return addBefore(predicate, ProxyListenerProvider.singleton(listener));
   }

   /**
    * Add the provider to the place after the place that {@code predicate} was matched.
    *
    * @param predicate the predicate to determine the place for the listener to be inserted
    * @param provider  the listener
    * @return the store itself
    */
   public ProxyListeners addBefore(Predicate<ProxyListenerProvider> predicate,
                                   ProxyListenerProvider provider) {
      int matched = IntStream.range(0, listeners.size())
            .filter(index -> predicate.test(listeners.get(index)))
            .findFirst()
            .orElseThrow(ProxyException.toThrow("Listener not exist in store: %s", predicate));
      listeners.add(matched, provider);
      return this;
   }

   /**
    * Add the listener to the last place of the store.
    *
    * @param listener the listener
    * @return the store itself
    */
   public ProxyListeners addLast(ProxyListener listener) {
      return addLast(ProxyListenerProvider.singleton(listener));
   }

   /**
    * Add the provider to the last place of the store.
    *
    * @param provider the listener
    * @return the store itself
    */
   public ProxyListeners addLast(ProxyListenerProvider provider) {
      listeners.add(provider);
      return this;
   }

   public List<ProxyListenerProvider> getListeners() {
      return listeners;
   }
}

package io.engytita.proxy.cache;

import java.util.concurrent.CompletionStage;

public interface ProxyCache<K, V> {
   CompletionStage<V> get(K key);

   CompletionStage<Void> put(K key, V value);
}

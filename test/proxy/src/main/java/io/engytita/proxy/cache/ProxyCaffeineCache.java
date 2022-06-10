package io.engytita.proxy.cache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class ProxyCaffeineCache<K, V> implements ProxyCache<K, V> {
   private final Cache<K, V> cache;

   public ProxyCaffeineCache() {
      cache = Caffeine.newBuilder().build();
   }

   @Override
   public CompletionStage<V> get(K key) {
      return CompletableFuture.completedFuture(cache.getIfPresent(key));
   }

   @Override
   public CompletionStage<Void> put(K key, V value) {
      cache.put(key, value);
      return CompletableFuture.completedFuture(null);
   }
}

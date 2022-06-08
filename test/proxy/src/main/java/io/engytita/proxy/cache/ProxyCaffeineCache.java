package io.engytita.proxy.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

public class ProxyCaffeineCache<K, V> implements ProxyCache<K, V> {
   private final Cache<K, V> cache;

   public ProxyCaffeineCache() {
      cache = Caffeine.newBuilder().build();
   }

   @Override
   public V get(K key) {
      return cache.getIfPresent(key);
   }

   @Override
   public void put(K key, V value) {
      cache.put(key, value);
   }
}

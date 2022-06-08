package io.engytita.proxy.cache;

public interface ProxyCache<K, V> {
   V get(K key);

   void put(K key, V value);
}

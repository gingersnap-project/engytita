package io.engytita.proxy.enums;

public enum ProxyMode {
   SOCKS,
   HTTP,
   TRANSPARENT;

   public static ProxyMode of(String name) {
      try {
         return ProxyMode.valueOf(name);
      } catch (Exception e) {
         throw new IllegalArgumentException("Illegal proxy mode: " + name);
      }
   }
}

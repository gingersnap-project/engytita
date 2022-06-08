package io.engytita.proxy.exception;

import static java.lang.String.format;

import java.util.function.Supplier;

public class ProxyException extends RuntimeException {

   public ProxyException(String message) {
      super(message);
   }

   public ProxyException(String message, Throwable cause) {
      super(message, cause);
   }

   public static Supplier<ProxyException> toThrow(String message, Object... args) {
      return () -> new ProxyException(format(message, args));
   }
}

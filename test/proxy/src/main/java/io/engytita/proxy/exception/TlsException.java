package io.engytita.proxy.exception;

public class TlsException extends ProxyException {

   public TlsException(String message) {
      super(message);
   }

   public TlsException(String message, Throwable cause) {
      super(message, cause);
   }
}

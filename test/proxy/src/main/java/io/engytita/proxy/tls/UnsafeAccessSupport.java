package io.engytita.proxy.tls;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManagerFactory;

import io.engytita.proxy.ConnectionContext;

public interface UnsafeAccessSupport {

   UnsafeAccessSupport DENY = new UnsafeAccessSupport() {
      @Override
      public UnsafeAccess checkUnsafeAccess(ConnectionContext context, X509Certificate[] chain,
                                            CertificateException cause) {
         return UnsafeAccess.DENY;
      }

      @Override
      public TrustManagerFactory create(TrustManagerFactory delegate, ConnectionContext context) {
         return delegate;
      }
   };

   UnsafeAccess checkUnsafeAccess(ConnectionContext context, X509Certificate[] chain, CertificateException cause);

   TrustManagerFactory create(TrustManagerFactory delegate, ConnectionContext context);
}

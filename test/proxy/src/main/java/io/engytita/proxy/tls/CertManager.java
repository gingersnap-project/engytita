package io.engytita.proxy.tls;

import java.util.concurrent.TimeUnit;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import io.engytita.proxy.ProxyConfig;

public class CertManager {

   private static final int CERT_CACHE_SIZE = 2000;

   private final X509CertificateHolder certificate;
   private final PrivateKeyInfo key;

   private final LoadingCache<String, Certificate> certsCache;

   public CertManager(ProxyConfig config) {
      this.certificate = config.getCertificate();
      this.key = config.getKey();
      this.certsCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.DAYS)
            .maximumSize(CERT_CACHE_SIZE)
            .build(this::createCert);
   }

   public Certificate getCert(String host) {
      return certsCache.get(host);
   }

   private Certificate createCert(String host) {
      return CertUtil.newCert(certificate, key, host);
   }
}

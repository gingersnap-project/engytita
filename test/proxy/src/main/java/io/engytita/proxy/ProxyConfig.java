package io.engytita.proxy;

import static java.util.Arrays.asList;

import java.security.Provider;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;

import io.engytita.proxy.enums.ProxyMode;
import io.engytita.proxy.handler.protocol.ProtocolDetector;
import io.engytita.proxy.handler.protocol.http1.Http1ProtocolDetector;
import io.engytita.proxy.listener.ProxyListeners;
import io.engytita.proxy.tls.UnsafeAccessSupport;

public class ProxyConfig {

   private ProxyMode proxyMode;

   private String host;
   private int port;

   private String remoteHost;
   private int remotePort;
   // TLS related
   private X509CertificateHolder certificate;
   private PrivateKeyInfo key;
   private boolean insecure;
   private Provider sslProvider;
   private List<String> tlsProtocols;
   private KeyManagerFactory clientKeyManagerFactory;
   private int maxContentLength;
   private ProxyStatusListener statusListener;
   private ProxyListeners listeners;
   private TrustManager trustManager;
   private UnsafeAccessSupport unsafeAccessSupport = UnsafeAccessSupport.DENY;
   private List<ProtocolDetector> detectors;
   // Default values
   public ProxyConfig() {
      proxyMode = ProxyMode.HTTP;

      host = "127.0.0.1";
      port = 9090;
      remoteHost = "127.0.0.1";
      remotePort = 8080;

      insecure = false;
      tlsProtocols = asList("TLSv1.3", "TLSv1.2");

      maxContentLength = 1024 * 1024;

      listeners = new ProxyListeners();
      detectors = Collections.singletonList(Http1ProtocolDetector.INSTANCE);
   }

   public String getRemoteHost() {
      return remoteHost;
   }

   public void setRemoteHost(String remoteHost) {
      this.remoteHost = remoteHost;
   }

   public int getRemotePort() {
      return remotePort;
   }

   public void setRemotePort(int remotePort) {
      this.remotePort = remotePort;
   }

   public ProxyMode getProxyMode() {
      return proxyMode;
   }

   public void setProxyMode(ProxyMode proxyMode) {
      this.proxyMode = proxyMode;
   }

   public String getHost() {
      return host;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public int getPort() {
      return port;
   }

   public void setPort(int port) {
      this.port = port;
   }

   public X509CertificateHolder getCertificate() {
      return certificate;
   }

   public void setCertificate(X509CertificateHolder certificate) {
      this.certificate = certificate;
   }

   public PrivateKeyInfo getKey() {
      return key;
   }

   public void setKey(PrivateKeyInfo key) {
      this.key = key;
   }

   public boolean isInsecure() {
      return insecure;
   }

   public void setInsecure(boolean insecure) {
      this.insecure = insecure;
   }

   public Provider getSslProvider() {
      return sslProvider;
   }

   public void setSslProvider(Provider sslProvider) {
      this.sslProvider = sslProvider;
   }

   public List<String> getTlsProtocols() {
      return tlsProtocols;
   }

   public void setTlsProtocols(List<String> tlsProtocols) {
      this.tlsProtocols = tlsProtocols;
   }

   public KeyManagerFactory getClientKeyManagerFactory() {
      return clientKeyManagerFactory;
   }

   public void setClientKeyManagerFactory(KeyManagerFactory clientKeyManagerFactory) {
      this.clientKeyManagerFactory = clientKeyManagerFactory;
   }

   public TrustManager getTrustManager() {
      return trustManager;
   }

   public void setTrustManager(TrustManager trustManager) {
      this.trustManager = trustManager;
   }

   public int getMaxContentLength() {
      return maxContentLength;
   }

   public void setMaxContentLength(int maxContentLength) {
      this.maxContentLength = maxContentLength;
   }

   public ProxyStatusListener getStatusListener() {
      return statusListener;
   }

   public void setStatusListener(ProxyStatusListener statusListener) {
      this.statusListener = statusListener;
   }

   public ProxyListeners getListeners() {
      return listeners;
   }

   public UnsafeAccessSupport getUnsafeAccessSupport() {
      return unsafeAccessSupport;
   }

   public void setUnsafeAccessSupport(UnsafeAccessSupport unsafeAccessSupport) {
      this.unsafeAccessSupport = unsafeAccessSupport;
   }

   public List<ProtocolDetector> getDetectors() {
      return detectors;
   }

   public void setDetectors(List<ProtocolDetector> detectors) {
      this.detectors = detectors;
   }

   @Override
   public String toString() {
      return "NitmProxyConfig{" +
            "proxyMode=" + proxyMode +
            ", host='" + host + '\'' +
            ", port=" + port +
            ", remoteHost='" + remoteHost + '\'' +
            ", remotePort=" + remotePort +
            ", certificate=" + certificate +
            ", key=" + key +
            ", insecure=" + insecure +
            ", sslProvider=" + sslProvider +
            ", tlsProtocols=" + tlsProtocols +
            ", clientKeyManagerFactory=" + clientKeyManagerFactory +
            ", maxContentLength=" + maxContentLength +
            ", statusListener=" + statusListener +
            ", listenerStore=" + listeners +
            ", trustManager=" + trustManager +
            ", unsafeAccessSupport=" + unsafeAccessSupport +
            ", detectors=" + detectors +
            '}';
   }
}
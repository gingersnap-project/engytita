package io.engytita.proxy.listener;

import java.util.List;
import java.util.Optional;

import io.engytita.proxy.ConnectionContext;
import io.engytita.proxy.cache.ProxyCache;
import io.engytita.proxy.cache.ProxyCaffeineCache;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.AttributeKey;

public class ProxyCacheListener implements ProxyListener {

   private final ProxyCache<String, FullHttpResponse> cache;
   private static final AttributeKey<String> URI_KEY = AttributeKey.newInstance("uri");

   public ProxyCacheListener() {
      cache = new ProxyCaffeineCache<>();
   }

   @Override
   public Optional<FullHttpResponse> onHttp1Request(ConnectionContext connectionContext, FullHttpRequest request) {
      connectionContext.clientChannel().attr(URI_KEY).set(request.uri());
      FullHttpResponse response = cache.get(request.uri());
      return response == null ? Optional.empty() : Optional.of(response.retain());
   }

   @Override
   public List<HttpObject> onHttp1Response(ConnectionContext connectionContext, HttpObject response) {
      if (response instanceof FullHttpResponse r) {
         String s = connectionContext.clientChannel().attr(URI_KEY).getAndSet(null);
         cache.put(s, r.retainedDuplicate());
      }
      return ProxyListener.super.onHttp1Response(connectionContext, response);
   }
}

package io.engytita.proxy.listener;

import java.util.List;
import java.util.concurrent.CompletionStage;

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
   public CompletionStage<FullHttpResponse> onHttp1Request(ConnectionContext connectionContext, FullHttpRequest request) {
      connectionContext.clientChannel().attr(URI_KEY).set(request.uri());
      return cache.get(request.uri()).thenApply(r -> r == null ? null : r.retain());
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

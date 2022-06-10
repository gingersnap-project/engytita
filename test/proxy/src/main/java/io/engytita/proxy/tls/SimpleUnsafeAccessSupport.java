package io.engytita.proxy.tls;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_HTML;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.net.ssl.TrustManagerFactory;

import io.engytita.proxy.Address;
import io.engytita.proxy.ConnectionContext;
import io.engytita.proxy.handler.protocol.http2.Http2FramesWrapper;
import io.engytita.proxy.listener.ProxyListener;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class SimpleUnsafeAccessSupport implements UnsafeAccessSupport {

   private static final String ACCEPT_MAGIC = ";nitmproxy-unsafe=accept";
   private static final String DENY_MAGIC = ";nitmproxy-unsafe=deny";

   private final ConcurrentMap<Address, UnsafeAccess> accepted;
   private final String askTemplate;
   private final Interceptor interceptor;

   @SuppressWarnings("UnstableApiUsage")
   public SimpleUnsafeAccessSupport() throws IOException {
      this("");
   }

   public SimpleUnsafeAccessSupport(String askTemplate) {
      this.accepted = new ConcurrentHashMap<>();
      this.askTemplate = askTemplate;
      this.interceptor = new Interceptor();
   }

   @Override
   public UnsafeAccess checkUnsafeAccess(ConnectionContext context, X509Certificate[] chain,
                                         CertificateException cause) {
      accepted.putIfAbsent(context.getServerAddr(), UnsafeAccess.ASK);
      return accepted.get(context.getServerAddr());
   }

   @Override
   public TrustManagerFactory create(TrustManagerFactory delegate, ConnectionContext context) {
      return UnsafeAccessSupportTrustManagerFactory.create(delegate, this, context);
   }

   public ProxyListener getInterceptor() {
      return interceptor;
   }

   class Interceptor implements ProxyListener {
      @Override
      public CompletionStage<FullHttpResponse> onHttp1Request(ConnectionContext connectionContext, FullHttpRequest request) {
         if (connectionContext.getServerAddr() == null || !accepted.containsKey(connectionContext.getServerAddr())) {
            return CompletableFuture.completedFuture(null);
         }
         return switch (accepted.get(connectionContext.getServerAddr())) {
            case ASK -> CompletableFuture.completedFuture(handleAskHttp1(connectionContext, request));
            case DENY -> CompletableFuture.completedFuture(createDenyResponse());
            case ACCEPT -> CompletableFuture.completedFuture(null);
         };
      }

      @Override
      public Optional<Http2FramesWrapper> onHttp2Request(ConnectionContext context, Http2FramesWrapper request) {
         if (context.getServerAddr() == null || !accepted.containsKey(context.getServerAddr())) {
            return Optional.empty();
         }
         return switch (accepted.get(context.getServerAddr())) {
            case ASK -> handleAskHttp2(context, request);
            case DENY -> Optional.of(Http2FramesWrapper
                  .builder(request.getStreamId())
                  .response(createDenyResponse())
                  .build());
            case ACCEPT -> Optional.empty();
         };
      }

      private FullHttpResponse handleAskHttp1(ConnectionContext context, FullHttpRequest request) {
         if (request.uri().endsWith(ACCEPT_MAGIC)) {
            request.setUri(request.uri().replace(ACCEPT_MAGIC, ""));
            accepted.put(context.getServerAddr(), UnsafeAccess.ACCEPT);
            return null;
         }
         if (request.uri().endsWith(DENY_MAGIC)) {
            accepted.put(context.getServerAddr(), UnsafeAccess.DENY);
            return createDenyResponse();
         }
         return createAskResponse(request.uri());
      }

      private Optional<Http2FramesWrapper> handleAskHttp2(
            ConnectionContext connectionContext,
            Http2FramesWrapper request) {
         String uri = request.getHeaders().headers().path().toString();
         if (uri.endsWith(ACCEPT_MAGIC)) {
            request.getHeaders().headers().path(uri.replace(ACCEPT_MAGIC, ""));
            accepted.put(connectionContext.getServerAddr(), UnsafeAccess.ACCEPT);
            return Optional.empty();
         }
         if (uri.endsWith(DENY_MAGIC)) {
            accepted.put(connectionContext.getServerAddr(), UnsafeAccess.DENY);
            return Optional.of(Http2FramesWrapper
                  .builder(request.getStreamId())
                  .response(createDenyResponse())
                  .build());
         }
         return Optional.of(Http2FramesWrapper
               .builder(request.getStreamId())
               .response(createAskResponse(request.getHeaders().headers().path().toString()))
               .build());
      }

      private FullHttpResponse createDenyResponse() {
         return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN);
      }

      private FullHttpResponse createAskResponse(String uri) {
         DefaultFullHttpResponse response = new DefaultFullHttpResponse(
               HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
         response.content().writeCharSequence(format(askTemplate, uri + ACCEPT_MAGIC, uri + DENY_MAGIC), UTF_8);
         response.headers().set(CONTENT_TYPE, TEXT_HTML);
         response.headers().set(CONTENT_LENGTH, response.content().readableBytes());
         return response;
      }
   }
}

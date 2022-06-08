package io.engytita.proxy.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import io.engytita.proxy.ConnectionContext;
import io.engytita.proxy.event.ForwardEvent;
import io.engytita.proxy.event.HttpEvent;
import io.engytita.proxy.handler.protocol.http2.Http2FrameWrapper;
import io.engytita.proxy.handler.protocol.http2.Http2FramesWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

public class ProxyListenerManager implements ProxyListener {

   private final List<ProxyListener> listeners;
   private final List<ProxyListener> reversedListeners;

   public ProxyListenerManager(List<ProxyListener> listeners) {
      this.listeners = Collections.unmodifiableList(listeners);
      this.reversedListeners = new ArrayList<>(this.listeners.size());
      for (int i = listeners.size() - 1; i >= 0; i--) {
         this.reversedListeners.add(listeners.get(i));
      }
   }

   @Override
   public void onInit(ConnectionContext connectionContext, Channel clientChannel) {
      listeners.forEach(listener -> listener.onInit(connectionContext, clientChannel));
   }

   @Override
   public void onConnect(ConnectionContext connectionContext, Channel serverChannel) {
      listeners.forEach(listener -> listener.onConnect(connectionContext, serverChannel));
   }

   @Override
   public void onHttpEvent(HttpEvent event) {
      listeners.forEach(listener -> listener.onHttpEvent(event));
   }

   @Override
   public Optional<FullHttpResponse> onHttp1Request(ConnectionContext connectionContext, FullHttpRequest request) {
      Function<ProxyListener, Stream<FullHttpResponse>> apply = listener -> listener
            .onHttp1Request(connectionContext, request).stream();
      return listeners.stream().flatMap(apply).findFirst();
   }

   @Override
   public List<HttpObject> onHttp1Response(ConnectionContext connectionContext, HttpObject response) {
      return reversedListeners.stream()
            .reduce(Collections.singletonList(response),
                  (objects, listener) -> objects.stream()
                        .flatMap(f -> listener.onHttp1Response(connectionContext, f).stream())
                        .toList(),
                  (accu, objects) -> objects);
   }

   @Override
   public Optional<Http2FramesWrapper> onHttp2Request(ConnectionContext connectionContext,
                                                      Http2FramesWrapper request) {
      Function<ProxyListener, Stream<Http2FramesWrapper>> apply = listener -> listener
            .onHttp2Request(connectionContext, request).stream();
      return listeners.stream().flatMap(apply).findFirst();
   }

   @Override
   public List<Http2FrameWrapper<?>> onHttp2Response(ConnectionContext connectionContext, Http2FrameWrapper<?> frame) {
      return reversedListeners.stream()
            .reduce(Collections.singletonList(frame),
                  (frames, listener) -> frames.stream()
                        .flatMap(f -> listener.onHttp2Response(connectionContext, f).stream())
                        .toList(),
                  (accu, frames) -> frames);
   }

   @Override
   public void onWsRequest(ConnectionContext connectionContext, WebSocketFrame frame) {
      listeners.forEach(listener -> listener.onWsRequest(connectionContext, frame));
   }

   @Override
   public void onWsResponse(ConnectionContext connectionContext, WebSocketFrame frame) {
      listeners.forEach(listener -> listener.onWsResponse(connectionContext, frame));
   }

   @Override
   public void onForwardEvent(ConnectionContext connectionContext, ForwardEvent event) {
      listeners.forEach(listener -> listener.onForwardEvent(connectionContext, event));
   }

   @Override
   public void onForwardRequest(ConnectionContext connectionContext, ByteBuf byteBuf) {
      listeners.forEach(listener -> listener.onForwardRequest(connectionContext, byteBuf));
   }

   @Override
   public void onForwardResponse(ConnectionContext connectionContext, ByteBuf byteBuf) {
      reversedListeners.forEach(listener -> listener.onForwardResponse(connectionContext, byteBuf));
   }

   @Override
   public void close(ConnectionContext connectionContext) {
      reversedListeners.forEach(listener -> listener.close(connectionContext));
   }
}

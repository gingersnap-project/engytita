package io.engytita.proxy.event;

import io.engytita.proxy.ConnectionContext;

public class OutboundChannelClosedEvent {
   private ConnectionContext connectionContext;
   private boolean client;

   public OutboundChannelClosedEvent(ConnectionContext connectionContext, boolean client) {
      this.connectionContext = connectionContext;
   }

   public ConnectionContext getConnectionInfo() {
      return connectionContext;
   }

   public boolean isClient() {
      return client;
   }

   @Override
   public String toString() {
      return String.format("%s : channelClosed", connectionContext);
   }
}

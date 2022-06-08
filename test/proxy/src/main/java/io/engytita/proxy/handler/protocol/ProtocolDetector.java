package io.engytita.proxy.handler.protocol;

import java.util.Optional;

import io.netty.buffer.ByteBuf;

public interface ProtocolDetector {

   Optional<String> detect(ByteBuf msg);

}

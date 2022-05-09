package io.engytita.test.client;

import org.infinispan.protostream.GeneratedSchema;
import org.infinispan.protostream.annotations.AutoProtoSchemaBuilder;

@AutoProtoSchemaBuilder(includeClasses = { Airport.class }, schemaPackageName = "io.engytita.test")
interface AirportSchema extends GeneratedSchema {
}
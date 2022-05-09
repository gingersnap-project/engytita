package io.engytita.test.server;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

public enum AirportType {
   SMALL("S"),
   MEDIUM("M"),
   LARGE("L"),
   HELIPAD("H"),
   WATER("W"),
   BALLOON("B"),
   CLOSED("C");

   final String code;

   AirportType(String code) {
      this.code = code;
   }

   public String toString() {
      return code;
   }

   static final Map<String, AirportType> MAP;

   static {
      MAP = new HashMap<>(AirportType.values().length);
      for (AirportType t : AirportType.values()) {
         MAP.put(t.code, t);
      }
   }

   public static AirportType fromCode(String code) {
      return MAP.get(code);
   }

   @Converter(autoApply = true)
   public static class AirportTypeConverter implements AttributeConverter<AirportType, String> {

      @Override
      public String convertToDatabaseColumn(AirportType airportType) {
         return airportType.toString();
      }

      @Override
      public AirportType convertToEntityAttribute(String code) {
         return AirportType.fromCode(code);
      }
   }

}

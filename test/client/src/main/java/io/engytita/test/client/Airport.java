package io.engytita.test.client;

import java.util.Objects;

import org.infinispan.protostream.annotations.ProtoField;

public class Airport {
   @ProtoField(number = 1)
   String ident;
   @ProtoField(number = 2)
   String type;
   @ProtoField(number = 3)
   String name;
   @ProtoField(number = 4, defaultValue = "0")
   int elevation;
   @ProtoField(number = 5)
   String continent;
   @ProtoField(number = 6)
   String isoCountry;
   @ProtoField(number = 7)
   String isoRegion;
   @ProtoField(number = 8)
   String municipality;
   @ProtoField(number = 9)
   String gpsCode;
   @ProtoField(number = 10)
   String iataCode;
   @ProtoField(number = 11)
   String localCode;

   public String getIdent() {
      return ident;
   }

   public void setIdent(String ident) {
      this.ident = ident;
   }

   public void setType(String type) {
      this.type = type;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setElevation(int elevation) {
      this.elevation = elevation;
   }

   public void setContinent(String continent) {
      this.continent = continent;
   }

   public void setIsoCountry(String isoCountry) {
      this.isoCountry = isoCountry;
   }

   public void setIsoRegion(String isoRegion) {
      this.isoRegion = isoRegion;
   }

   public void setMunicipality(String municipality) {
      this.municipality = municipality;
   }

   public void setGpsCode(String gpsCode) {
      this.gpsCode = gpsCode;
   }

   public void setIataCode(String iataCode) {
      this.iataCode = iataCode;
   }

   public void setLocalCode(String localCode) {
      this.localCode = localCode;
   }

   public String getType() {
      return type;
   }

   public String getName() {
      return name;
   }

   public int getElevation() {
      return elevation;
   }

   public String getContinent() {
      return continent;
   }

   public String getIsoCountry() {
      return isoCountry;
   }

   public String getIsoRegion() {
      return isoRegion;
   }

   public String getMunicipality() {
      return municipality;
   }

   public String getGpsCode() {
      return gpsCode;
   }

   public String getIataCode() {
      return iataCode;
   }

   public String getLocalCode() {
      return localCode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Airport airport = (Airport) o;
      return ident.equals(airport.ident);
   }

   @Override
   public int hashCode() {
      return Objects.hash(ident);
   }
}

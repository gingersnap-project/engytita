package io.engytita.test.server;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "airports")
public class Airport {
   @Id
   @Column(name = "ident", length = 12)
   private String ident;
   @Convert(converter = AirportType.AirportTypeConverter.class)
   @Column(name = "type", length = 1, columnDefinition = "bpchar")
   private AirportType type;
   @Column(name = "name", length = 100)
   private String name;
   @Column(name = "elevation")
   private int elevation;
   @Column(name = "continent", length = 2, columnDefinition = "bpchar")
   private String continent;
   @Column(name = "iso_country", length = 2, columnDefinition = "bpchar")
   private String isoCountry;
   @Column(name = "iso_region", length = 8)
   private String isoRegion;
   @Column(name = "municipality", length = 100)
   private String municipality;
   @Column(name = "gps_code", length = 12)
   private String gpsCode;
   @Column(name = "iata_code", length = 12)
   private String iataCode;
   @Column(name = "local_code", length = 12)
   private String localCode;

   public String getIdent() {
      return ident;
   }

   public AirportType getType() {
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

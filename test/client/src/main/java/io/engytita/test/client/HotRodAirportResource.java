package io.engytita.test.client;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.infinispan.client.hotrod.RemoteCache;

import io.quarkus.infinispan.client.Remote;
import io.smallrye.mutiny.Uni;

@Path("/ahotrodx")
public class HotRodAirportResource {
   @Inject
   @Remote("airports")
   RemoteCache<String, Airport> cache;

   @RestClient
   AirportService airportService;

   @GET
   @Path("{id}")
   public Uni<Airport> id(String id) {
      return Uni.createFrom().completionStage(cache.getAsync(id)).flatMap(response -> {
         if (response != null) {
            return Uni.createFrom().item(response);
         } else {
            return airportService.getById(id).call(airport -> Uni.createFrom().completionStage(cache.putAsync(id, airport)).map(v -> airport));
         }
      });
   }
}
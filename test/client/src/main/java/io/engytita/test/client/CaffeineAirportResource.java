package io.engytita.test.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;

@Path("/acaffein")
public class CaffeineAirportResource {

    @RestClient
    AirportService airportService;


    @GET
    @Path("{id}")
    @CacheResult(cacheName = "airports")
    public Uni<Airport> id(String id) {
        return airportService.getById(id);
    }
}
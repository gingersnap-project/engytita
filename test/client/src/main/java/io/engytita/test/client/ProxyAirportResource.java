package io.engytita.test.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.mutiny.Uni;

@Path("/airproxy")
public class ProxyAirportResource {

    @RestClient
    AirportProxyService airportService;


    @GET
    @Path("{id}")
    public Uni<Airport> id(String id) {
        return airportService.getById(id);
    }
}
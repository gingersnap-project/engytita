package io.engytita.test.client;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.smallrye.mutiny.Uni;

@Path("/airports-direct")
public class DirectAirportResource {

    @RestClient
    AirportService airportService;


    @GET
    @Path("{id}")
    public Uni<Airport> id(String id) {
        return airportService.getById(id);
    }
}
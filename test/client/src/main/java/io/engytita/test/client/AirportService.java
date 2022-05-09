package io.engytita.test.client;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;

@Path("/airports")
@RegisterRestClient(configKey = "airports")
public interface AirportService {

   @GET
   @Path("/{id}")
   Uni<Airport> getById(@PathParam("id") String id);
}

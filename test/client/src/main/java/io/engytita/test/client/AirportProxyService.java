package io.engytita.test.client;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.smallrye.mutiny.Uni;

@Path("/airports")
@RegisterRestClient(configKey = "airports-proxy")
public interface AirportProxyService {

   @GET
   @Path("/{id}")
   Uni<Airport> getById(@PathParam("id") String id);
}

package io.engytita.test.server;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/airports")
public class AirportResource {

    @Inject
    EntityManager em;

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Airport airport(@PathParam("id") String id) {
        return em.find(Airport.class, id);
    }
}

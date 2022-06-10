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

    static final String LILY = "{\"ident\":\"LILY\",\"type\":\"WATER\",\"name\":\"Como (Idroscalo - Water Ad) Hidroport\",\"elevation\":663,\"continent\":\"EU\",\"isoCountry\":\"IT\",\"isoRegion\":\"IT-25\",\"municipality\":\"Como (CO)\",\"gpsCode\":\"LILY\",\"iataCode\":null,\"localCode\":\"CO02\"}";

    @Inject
    EntityManager em;

    /*
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
     */
    public Airport airport(@PathParam("id") String id) {
        return em.find(Airport.class, id);
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public String staticAirport(@PathParam("id") String id) {
        return LILY;
    }
}

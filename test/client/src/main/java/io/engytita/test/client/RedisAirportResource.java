package io.engytita.test.client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Uni;

@Path("/airports-redis")
public class RedisAirportResource {
    @Inject
    ReactiveRedisClient redis;

    @RestClient
    AirportService airportService;

    @Inject
    ObjectMapper mapper;


    @GET
    @Path("{id}")
    public Uni<Airport> id(String id) {
        return redis.get(id).flatMap(response -> {
            if (response != null) {
                try {
                    return Uni.createFrom().item(mapper.readValue(response.toString(), Airport.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return airportService.getById(id).call(airport -> {
                    try {
                        return redis.set(Arrays.asList(id, mapper.writeValueAsString(airport))).map(v -> airport);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }
}
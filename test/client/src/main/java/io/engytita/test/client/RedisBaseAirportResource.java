package io.engytita.test.client;

import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Uni;

public abstract class RedisBaseAirportResource {

    @RestClient
    AirportService airportService;

    @Inject
    ObjectMapper mapper;

    protected abstract ReactiveRedisClient redisClient();

    @GET
    @Path("{id}")
    public Uni<Airport> id(String id) {
        return redisClient().get(id).flatMap(response -> {
            if (response != null) {
                try {
                    return Uni.createFrom().item(mapper.readValue(response.toString(), Airport.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return airportService.getById(id).call(airport -> {
                    try {
                        return redisClient().set(Arrays.asList(id, mapper.writeValueAsString(airport))).map(v -> airport);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }
}
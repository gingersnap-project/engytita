package io.engytita.test.client;

import javax.inject.Inject;
import javax.ws.rs.Path;

import io.quarkus.redis.client.RedisClientName;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;

@Path("/arespalt")
public class RedisAlternateAirportResource extends RedisBaseAirportResource {
    @Inject
    @RedisClientName("alternate")
    ReactiveRedisClient redis;

    @Override
    protected ReactiveRedisClient redisClient() {
        return redis;
    }
}

package com.maxiceballos.ecosystem.a.cache;

import java.io.UncheckedIOException;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxiceballos.ecosystem.a.dto.FooEvent;

@Component
public class FooEventCache {

    static final String KEY = "foo:last-event";

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    public FooEventCache(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    public void save(FooEvent event) {
        try {
            redis.opsForValue().set(KEY, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<String> getLastRaw() {
        return Optional.ofNullable(redis.opsForValue().get(KEY));
    }

    public Optional<FooEvent> getLastEvent() {
        return getLastRaw().map(this::readEvent);
    }

    private FooEvent readEvent(String json) {
        try {
            return objectMapper.readValue(json, FooEvent.class);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }
}

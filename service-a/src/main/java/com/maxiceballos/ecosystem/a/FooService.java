package com.maxiceballos.ecosystem.a;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.maxiceballos.ecosystem.a.cache.FooEventCache;
import com.maxiceballos.ecosystem.a.client.ServiceBClient;
import com.maxiceballos.ecosystem.a.dto.FooEvent;
import com.maxiceballos.ecosystem.a.dto.FooResponse;

@Service
public class FooService {

    private final ServiceBClient serviceBClient;
    private final FooEventCache fooEventCache;

    public FooService(ServiceBClient serviceBClient, FooEventCache fooEventCache) {
        this.serviceBClient = serviceBClient;
        this.fooEventCache = fooEventCache;
    }

    public FooResponse getFoo() {
        String cache = fooEventCache.getLastRaw().orElse("empty");

        FooResponse downstream = serviceBClient.getFoo();

        return new FooResponse("A", "foo", null, cache, downstream);
    }

    public Optional<FooEvent> lastEvent() {
        return fooEventCache.getLastEvent();
    }
}

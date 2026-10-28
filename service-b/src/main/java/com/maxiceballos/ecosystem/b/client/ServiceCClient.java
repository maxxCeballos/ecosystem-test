package com.maxiceballos.ecosystem.b.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.maxiceballos.ecosystem.b.dto.FooResponse;

@Component
public class ServiceCClient {

    private final RestClient restClient;

    public ServiceCClient(@Value("${clients.service-c.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public FooResponse getFoo() {
        return restClient.get()
                .uri("/foo")
                .retrieve()
                .body(FooResponse.class);
    }
}

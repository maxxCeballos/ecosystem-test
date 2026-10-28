package com.maxiceballos.ecosystem.a.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.maxiceballos.ecosystem.a.dto.FooResponse;

@Component
public class ServiceBClient {

    private final RestClient restClient;

    public ServiceBClient(@Value("${clients.service-b.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public FooResponse getFoo() {
        return restClient.get()
                .uri("/foo")
                .retrieve()
                .body(FooResponse.class);
    }
}

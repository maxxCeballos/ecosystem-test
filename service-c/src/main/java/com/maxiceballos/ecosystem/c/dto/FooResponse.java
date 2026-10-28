package com.maxiceballos.ecosystem.c.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FooResponse(
        String service,
        String message,
        Boolean db,
        String cache,
        FooResponse downstream
) {
}

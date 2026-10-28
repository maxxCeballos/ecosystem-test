package com.maxiceballos.ecosystem.a.dto;

import java.time.Instant;

public record FooEvent(String source, String message, Instant occurredAt) {
}

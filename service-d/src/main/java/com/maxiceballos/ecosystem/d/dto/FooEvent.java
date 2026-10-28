package com.maxiceballos.ecosystem.d.dto;

import java.time.Instant;

public record FooEvent(String source, String message, Instant occurredAt) {
}

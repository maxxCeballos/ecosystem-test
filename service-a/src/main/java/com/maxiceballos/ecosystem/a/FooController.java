package com.maxiceballos.ecosystem.a;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiceballos.ecosystem.a.dto.FooEvent;
import com.maxiceballos.ecosystem.a.dto.FooResponse;

@RestController
public class FooController {

    private final FooService fooService;

    public FooController(FooService fooService) {
        this.fooService = fooService;
    }

    @GetMapping("/foo")
    public FooResponse foo() {
        return fooService.getFoo();
    }

    @GetMapping("/events")
    public ResponseEntity<FooEvent> events() {
        return fooService.lastEvent()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }
}

package com.maxiceballos.ecosystem.c;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiceballos.ecosystem.c.dto.FooResponse;

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
}

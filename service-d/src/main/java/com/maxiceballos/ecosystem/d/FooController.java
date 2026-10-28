package com.maxiceballos.ecosystem.d;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.maxiceballos.ecosystem.d.dto.FooPublishResponse;
import com.maxiceballos.ecosystem.d.dto.FooRequest;

@RestController
public class FooController {

    private final FooService fooService;

    public FooController(FooService fooService) {
        this.fooService = fooService;
    }

    @PostMapping("/foo")
    public FooPublishResponse publishFoo(@RequestBody(required = false) FooRequest request) {
        return fooService.publishFoo(request);
    }
}

package com.maxiceballos.ecosystem.a.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.maxiceballos.ecosystem.a.cache.FooEventCache;
import com.maxiceballos.ecosystem.a.dto.FooEvent;

@Component
public class FooEventListener {

    private final FooEventCache fooEventCache;

    public FooEventListener(FooEventCache fooEventCache) {
        this.fooEventCache = fooEventCache;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void onMessage(FooEvent event) {
        fooEventCache.save(event);
    }
}

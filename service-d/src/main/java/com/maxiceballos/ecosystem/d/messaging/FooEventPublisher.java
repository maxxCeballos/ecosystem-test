package com.maxiceballos.ecosystem.d.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.maxiceballos.ecosystem.d.dto.FooEvent;

@Component
public class FooEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public FooEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(FooEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);
    }
}

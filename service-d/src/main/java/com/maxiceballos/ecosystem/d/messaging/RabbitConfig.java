package com.maxiceballos.ecosystem.d.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "foo.events";
    public static final String QUEUE = "service-a.foo.events";
    public static final String ROUTING_KEY = "foo.created";

    @Bean
    TopicExchange fooEventsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    Queue fooEventsQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    Binding fooEventsBinding(Queue fooEventsQueue, TopicExchange fooEventsExchange) {
        return BindingBuilder.bind(fooEventsQueue).to(fooEventsExchange).with(ROUTING_KEY);
    }

    @Bean
    Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}

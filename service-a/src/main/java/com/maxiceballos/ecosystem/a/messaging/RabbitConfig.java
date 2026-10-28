package com.maxiceballos.ecosystem.a.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
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
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.setTrustedPackages("com.maxiceballos.ecosystem");
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }
}

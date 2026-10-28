package com.maxiceballos.ecosystem.a;

import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.maxiceballos.ecosystem.a.client.ServiceBClient;
import com.maxiceballos.ecosystem.a.dto.FooEvent;
import com.maxiceballos.ecosystem.a.messaging.RabbitConfig;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class FooRabbitIT {

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbit = new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-alpine"));

    @Container
    @ServiceConnection
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private ServiceBClient serviceBClient;

    @Test
    void publishedFooEventIsConsumedAndExposed() throws Exception {
        // ARRANGE
        FooEvent event = new FooEvent("D", "foo", Instant.parse("2026-08-26T12:00:00Z"));

        mockMvc.perform(get("/events")).andExpect(status().isNoContent());

        // ACT
        rabbitTemplate.convertAndSend(RabbitConfig.EXCHANGE, RabbitConfig.ROUTING_KEY, event);

        // ASSERT
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                mockMvc.perform(get("/events"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.source").value("D"))
                        .andExpect(jsonPath("$.message").value("foo"))
        );
    }
}

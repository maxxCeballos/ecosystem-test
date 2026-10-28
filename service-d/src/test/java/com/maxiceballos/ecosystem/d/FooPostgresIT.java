package com.maxiceballos.ecosystem.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.maxiceballos.ecosystem.d.data.FooRecordRepository;
import com.maxiceballos.ecosystem.d.dto.FooEvent;
import com.maxiceballos.ecosystem.d.messaging.FooEventPublisher;

@SpringBootTest(
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration"
)
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class FooPostgresIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FooRecordRepository fooRecordRepository;

    @MockitoBean
    private FooEventPublisher fooEventPublisher;

    @Test
    void postFooPersistsRecordInPostgres() throws Exception {
        // ARRANGE
        long countBefore = fooRecordRepository.count();

        // ACT
        ResultActions result = mockMvc.perform(
                post("/foo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"hello\"}")
        );

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("D"))
                .andExpect(jsonPath("$.message").value("hello"))
                .andExpect(jsonPath("$.db").value(true))
                .andExpect(jsonPath("$.published").value(true));
        assertEquals(countBefore + 1, fooRecordRepository.count());
        assertTrue(fooRecordRepository.findAll().stream().anyMatch(record -> "hello".equals(record.getPayload())));
        verify(fooEventPublisher).publish(any(FooEvent.class));
    }
}

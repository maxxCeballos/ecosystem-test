package com.maxiceballos.ecosystem.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.maxiceballos.ecosystem.c.data.FooRecordRepository;

@SpringBootTest
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

    @Test
    void getFooPersistsRecordInPostgres() throws Exception {
        // ARRANGE
        long countBefore = fooRecordRepository.count();

        // ACT
        ResultActions result = mockMvc.perform(get("/foo"));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("C"))
                .andExpect(jsonPath("$.message").value("foo"))
                .andExpect(jsonPath("$.db").value(true));
        assertEquals(countBefore + 1, fooRecordRepository.count());
        assertTrue(fooRecordRepository.findAll().stream().anyMatch(record -> "foo".equals(record.getPayload())));
    }
}

package com.maxiceballos.ecosystem.b;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.maxiceballos.ecosystem.b.client.ServiceCClient;
import com.maxiceballos.ecosystem.b.data.FooRecordRepository;
import com.maxiceballos.ecosystem.b.dto.FooResponse;

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

    @MockitoBean
    private ServiceCClient serviceCClient;

    @Test
    void getFooPersistsRecordInPostgres() throws Exception {
        // ARRANGE
        FooResponse fromC = new FooResponse("C", "foo", true, null, null);
        when(serviceCClient.getFoo()).thenReturn(fromC);
        long countBefore = fooRecordRepository.count();

        // ACT
        ResultActions result = mockMvc.perform(get("/foo"));

        // ASSERT
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.service").value("B"))
                .andExpect(jsonPath("$.message").value("foo"))
                .andExpect(jsonPath("$.db").value(true))
                .andExpect(jsonPath("$.downstream.service").value("C"));
        assertEquals(countBefore + 1, fooRecordRepository.count());
        assertTrue(fooRecordRepository.findAll().stream().anyMatch(record -> "foo".equals(record.getPayload())));
        verify(serviceCClient).getFoo();
    }
}

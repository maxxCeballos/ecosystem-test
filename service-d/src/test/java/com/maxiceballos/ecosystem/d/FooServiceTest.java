package com.maxiceballos.ecosystem.d;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maxiceballos.ecosystem.d.data.FooRecord;
import com.maxiceballos.ecosystem.d.data.FooRecordRepository;
import com.maxiceballos.ecosystem.d.dto.FooEvent;
import com.maxiceballos.ecosystem.d.dto.FooPublishResponse;
import com.maxiceballos.ecosystem.d.dto.FooRequest;
import com.maxiceballos.ecosystem.d.messaging.FooEventPublisher;

@ExtendWith(MockitoExtension.class)
class FooServiceTest {

    @Mock
    private FooRecordRepository fooRecordRepository;

    @Mock
    private FooEventPublisher fooEventPublisher;

    @InjectMocks
    private FooService fooService;

    @Test
    void publishFooPersistsAndPublishesEvent() {
        // ARRANGE
        FooRequest request = new FooRequest("hello");

        when(fooRecordRepository.save(any(FooRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        FooPublishResponse response = fooService.publishFoo(request);

        // ASSERT
        assertEquals("D", response.service());
        assertEquals("hello", response.message());
        assertTrue(response.db());
        assertTrue(response.published());
        verify(fooRecordRepository).save(any(FooRecord.class));
        verify(fooEventPublisher).publish(any(FooEvent.class));
    }
}

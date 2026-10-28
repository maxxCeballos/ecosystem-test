package com.maxiceballos.ecosystem.a;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maxiceballos.ecosystem.a.cache.FooEventCache;
import com.maxiceballos.ecosystem.a.client.ServiceBClient;
import com.maxiceballos.ecosystem.a.dto.FooResponse;

@ExtendWith(MockitoExtension.class)
class FooServiceTest {

    @Mock
    private ServiceBClient serviceBClient;

    @Mock
    private FooEventCache fooEventCache;

    @InjectMocks
    private FooService fooService;

    @Test
    void getFooCallsServiceBAndIncludesCache() {
        // ARRANGE
        FooResponse fromC = new FooResponse("C", "foo", true, null, null);
        FooResponse fromB = new FooResponse("B", "foo", true, null, fromC);

        when(fooEventCache.getLastRaw()).thenReturn(Optional.of("cached-event"));
        when(serviceBClient.getFoo()).thenReturn(fromB);

        // ACT
        FooResponse response = fooService.getFoo();

        // ASSERT
        assertEquals("A", response.service());
        assertEquals("foo", response.message());
        assertEquals("cached-event", response.cache());
        assertEquals(fromB, response.downstream());
        verify(serviceBClient).getFoo();
        verify(fooEventCache).getLastRaw();
    }
}

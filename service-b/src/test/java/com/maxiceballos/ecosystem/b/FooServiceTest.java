package com.maxiceballos.ecosystem.b;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maxiceballos.ecosystem.b.client.ServiceCClient;
import com.maxiceballos.ecosystem.b.data.FooRecord;
import com.maxiceballos.ecosystem.b.data.FooRecordRepository;
import com.maxiceballos.ecosystem.b.dto.FooResponse;

@ExtendWith(MockitoExtension.class)
class FooServiceTest {

    @Mock
    private FooRecordRepository fooRecordRepository;

    @Mock
    private ServiceCClient serviceCClient;

    @InjectMocks
    private FooService fooService;

    @Test
    void getFooPersistsCallsServiceCAndNestsResponse() {
        // ARRANGE
        FooResponse fromC = new FooResponse("C", "foo", true, null, null);

        when(fooRecordRepository.save(any(FooRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(serviceCClient.getFoo()).thenReturn(fromC);

        // ACT
        FooResponse response = fooService.getFoo();

        // ASSERT
        assertEquals("B", response.service());
        assertEquals("foo", response.message());
        assertEquals(true, response.db());
        assertEquals(fromC, response.downstream());
        verify(fooRecordRepository).save(any(FooRecord.class));
        verify(serviceCClient).getFoo();
    }
}

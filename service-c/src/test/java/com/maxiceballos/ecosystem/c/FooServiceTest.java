package com.maxiceballos.ecosystem.c;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.maxiceballos.ecosystem.c.data.FooRecord;
import com.maxiceballos.ecosystem.c.data.FooRecordRepository;
import com.maxiceballos.ecosystem.c.dto.FooResponse;

@ExtendWith(MockitoExtension.class)
class FooServiceTest {

    @Mock
    private FooRecordRepository fooRecordRepository;

    @InjectMocks
    private FooService fooService;

    @Test
    void getFooPersistsAndReturnsFoo() {
        // ARRANGE
        when(fooRecordRepository.save(any(FooRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // ACT
        FooResponse response = fooService.getFoo();

        // ASSERT
        assertEquals("C", response.service());
        assertEquals("foo", response.message());
        assertEquals(true, response.db());
        assertNull(response.downstream());
        verify(fooRecordRepository).save(any(FooRecord.class));
    }
}

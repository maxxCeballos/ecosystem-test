package com.maxiceballos.ecosystem.c;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.maxiceballos.ecosystem.c.data.FooRecord;
import com.maxiceballos.ecosystem.c.data.FooRecordRepository;
import com.maxiceballos.ecosystem.c.dto.FooResponse;

@Service
public class FooService {

    private final FooRecordRepository fooRecordRepository;

    public FooService(FooRecordRepository fooRecordRepository) {
        this.fooRecordRepository = fooRecordRepository;
    }

    public FooResponse getFoo() {
        fooRecordRepository.save(new FooRecord("foo", Instant.now()));

        return new FooResponse("C", "foo", true, null, null);
    }
}

package com.maxiceballos.ecosystem.d;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.maxiceballos.ecosystem.d.data.FooRecord;
import com.maxiceballos.ecosystem.d.data.FooRecordRepository;
import com.maxiceballos.ecosystem.d.dto.FooEvent;
import com.maxiceballos.ecosystem.d.dto.FooPublishResponse;
import com.maxiceballos.ecosystem.d.dto.FooRequest;
import com.maxiceballos.ecosystem.d.messaging.FooEventPublisher;

@Service
public class FooService {

    private final FooRecordRepository fooRecordRepository;
    private final FooEventPublisher fooEventPublisher;

    public FooService(FooRecordRepository fooRecordRepository, FooEventPublisher fooEventPublisher) {
        this.fooRecordRepository = fooRecordRepository;
        this.fooEventPublisher = fooEventPublisher;
    }

    public FooPublishResponse publishFoo(FooRequest request) {
        String message = request != null && request.message() != null ? request.message() : "foo";

        fooRecordRepository.save(new FooRecord(message, Instant.now()));

        FooEvent event = new FooEvent("D", message, Instant.now());
        fooEventPublisher.publish(event);

        return new FooPublishResponse("D", message, true, true);
    }
}

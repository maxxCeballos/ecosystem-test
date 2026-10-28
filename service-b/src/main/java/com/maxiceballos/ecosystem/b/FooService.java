package com.maxiceballos.ecosystem.b;

import java.time.Instant;

import org.springframework.stereotype.Service;

import com.maxiceballos.ecosystem.b.client.ServiceCClient;
import com.maxiceballos.ecosystem.b.data.FooRecord;
import com.maxiceballos.ecosystem.b.data.FooRecordRepository;
import com.maxiceballos.ecosystem.b.dto.FooResponse;

@Service
public class FooService {

    private final FooRecordRepository fooRecordRepository;
    private final ServiceCClient serviceCClient;

    public FooService(FooRecordRepository fooRecordRepository, ServiceCClient serviceCClient) {
        this.fooRecordRepository = fooRecordRepository;
        this.serviceCClient = serviceCClient;
    }

    public FooResponse getFoo() {
        fooRecordRepository.save(new FooRecord("foo", Instant.now()));

        FooResponse downstream = serviceCClient.getFoo();

        return new FooResponse("B", "foo", true, null, downstream);
    }
}

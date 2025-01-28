package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EntityHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EntityHistory getEntityHistorySample1() {
        return new EntityHistory().id(1L).userLogin("userLogin1").entityName("entityName1").entityId(1L);
    }

    public static EntityHistory getEntityHistorySample2() {
        return new EntityHistory().id(2L).userLogin("userLogin2").entityName("entityName2").entityId(2L);
    }

    public static EntityHistory getEntityHistoryRandomSampleGenerator() {
        return new EntityHistory()
            .id(longCount.incrementAndGet())
            .userLogin(UUID.randomUUID().toString())
            .entityName(UUID.randomUUID().toString())
            .entityId(longCount.incrementAndGet());
    }
}

package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EntityBTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EntityB getEntityBSample1() {
        return new EntityB().id(1L).firstName("firstName1").lastName("lastName1");
    }

    public static EntityB getEntityBSample2() {
        return new EntityB().id(2L).firstName("firstName2").lastName("lastName2");
    }

    public static EntityB getEntityBRandomSampleGenerator() {
        return new EntityB().id(longCount.incrementAndGet()).firstName(UUID.randomUUID().toString()).lastName(UUID.randomUUID().toString());
    }
}

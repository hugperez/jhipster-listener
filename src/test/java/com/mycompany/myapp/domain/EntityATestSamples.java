package com.mycompany.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EntityATestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static EntityA getEntityASample1() {
        return new EntityA().id(1L).name("name1").title("title1").description("description1");
    }

    public static EntityA getEntityASample2() {
        return new EntityA().id(2L).name("name2").title("title2").description("description2");
    }

    public static EntityA getEntityARandomSampleGenerator() {
        return new EntityA()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}

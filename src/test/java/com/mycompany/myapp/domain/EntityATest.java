package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EntityATestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EntityATest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EntityA.class);
        EntityA entityA1 = getEntityASample1();
        EntityA entityA2 = new EntityA();
        assertThat(entityA1).isNotEqualTo(entityA2);

        entityA2.setId(entityA1.getId());
        assertThat(entityA1).isEqualTo(entityA2);

        entityA2 = getEntityASample2();
        assertThat(entityA1).isNotEqualTo(entityA2);
    }
}

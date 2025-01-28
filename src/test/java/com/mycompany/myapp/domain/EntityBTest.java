package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EntityBTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EntityBTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EntityB.class);
        EntityB entityB1 = getEntityBSample1();
        EntityB entityB2 = new EntityB();
        assertThat(entityB1).isNotEqualTo(entityB2);

        entityB2.setId(entityB1.getId());
        assertThat(entityB1).isEqualTo(entityB2);

        entityB2 = getEntityBSample2();
        assertThat(entityB1).isNotEqualTo(entityB2);
    }
}

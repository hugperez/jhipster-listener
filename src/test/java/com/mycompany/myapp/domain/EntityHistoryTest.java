package com.mycompany.myapp.domain;

import static com.mycompany.myapp.domain.EntityHistoryTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.mycompany.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EntityHistoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EntityHistory.class);
        EntityHistory entityHistory1 = getEntityHistorySample1();
        EntityHistory entityHistory2 = new EntityHistory();
        assertThat(entityHistory1).isNotEqualTo(entityHistory2);

        entityHistory2.setId(entityHistory1.getId());
        assertThat(entityHistory1).isEqualTo(entityHistory2);

        entityHistory2 = getEntityHistorySample2();
        assertThat(entityHistory1).isNotEqualTo(entityHistory2);
    }
}

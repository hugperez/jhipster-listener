package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.EntityBAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.EntityB;
import com.mycompany.myapp.repository.EntityBRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EntityBResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EntityBResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/entity-bs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityBRepository entityBRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEntityBMockMvc;

    private EntityB entityB;

    private EntityB insertedEntityB;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntityB createEntity() {
        return new EntityB().firstName(DEFAULT_FIRST_NAME).lastName(DEFAULT_LAST_NAME);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntityB createUpdatedEntity() {
        return new EntityB().firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);
    }

    @BeforeEach
    public void initTest() {
        entityB = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEntityB != null) {
            entityBRepository.delete(insertedEntityB);
            insertedEntityB = null;
        }
    }

    @Test
    @Transactional
    void createEntityB() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EntityB
        var returnedEntityB = om.readValue(
            restEntityBMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityB)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EntityB.class
        );

        // Validate the EntityB in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEntityBUpdatableFieldsEquals(returnedEntityB, getPersistedEntityB(returnedEntityB));

        insertedEntityB = returnedEntityB;
    }

    @Test
    @Transactional
    void createEntityBWithExistingId() throws Exception {
        // Create the EntityB with an existing ID
        entityB.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEntityBMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityB)))
            .andExpect(status().isBadRequest());

        // Validate the EntityB in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEntityBS() throws Exception {
        // Initialize the database
        insertedEntityB = entityBRepository.saveAndFlush(entityB);

        // Get all the entityBList
        restEntityBMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entityB.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)));
    }

    @Test
    @Transactional
    void getEntityB() throws Exception {
        // Initialize the database
        insertedEntityB = entityBRepository.saveAndFlush(entityB);

        // Get the entityB
        restEntityBMockMvc
            .perform(get(ENTITY_API_URL_ID, entityB.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(entityB.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME));
    }

    @Test
    @Transactional
    void getNonExistingEntityB() throws Exception {
        // Get the entityB
        restEntityBMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEntityB() throws Exception {
        // Initialize the database
        insertedEntityB = entityBRepository.saveAndFlush(entityB);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityB
        EntityB updatedEntityB = entityBRepository.findById(entityB.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEntityB are not directly saved in db
        em.detach(updatedEntityB);
        updatedEntityB.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restEntityBMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEntityB.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEntityB))
            )
            .andExpect(status().isOk());

        // Validate the EntityB in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEntityBToMatchAllProperties(updatedEntityB);
    }

    @Test
    @Transactional
    void putNonExistingEntityB() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityB.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntityBMockMvc
            .perform(put(ENTITY_API_URL_ID, entityB.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityB)))
            .andExpect(status().isBadRequest());

        // Validate the EntityB in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEntityB() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityB.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityBMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entityB))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityB in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEntityB() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityB.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityBMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityB)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EntityB in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEntityBWithPatch() throws Exception {
        // Initialize the database
        insertedEntityB = entityBRepository.saveAndFlush(entityB);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityB using partial update
        EntityB partialUpdatedEntityB = new EntityB();
        partialUpdatedEntityB.setId(entityB.getId());

        restEntityBMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntityB.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntityB))
            )
            .andExpect(status().isOk());

        // Validate the EntityB in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntityBUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEntityB, entityB), getPersistedEntityB(entityB));
    }

    @Test
    @Transactional
    void fullUpdateEntityBWithPatch() throws Exception {
        // Initialize the database
        insertedEntityB = entityBRepository.saveAndFlush(entityB);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityB using partial update
        EntityB partialUpdatedEntityB = new EntityB();
        partialUpdatedEntityB.setId(entityB.getId());

        partialUpdatedEntityB.firstName(UPDATED_FIRST_NAME).lastName(UPDATED_LAST_NAME);

        restEntityBMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntityB.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntityB))
            )
            .andExpect(status().isOk());

        // Validate the EntityB in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntityBUpdatableFieldsEquals(partialUpdatedEntityB, getPersistedEntityB(partialUpdatedEntityB));
    }

    @Test
    @Transactional
    void patchNonExistingEntityB() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityB.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntityBMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, entityB.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(entityB))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityB in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEntityB() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityB.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityBMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(entityB))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityB in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEntityB() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityB.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityBMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(entityB)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EntityB in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEntityB() throws Exception {
        // Initialize the database
        insertedEntityB = entityBRepository.saveAndFlush(entityB);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the entityB
        restEntityBMockMvc
            .perform(delete(ENTITY_API_URL_ID, entityB.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return entityBRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected EntityB getPersistedEntityB(EntityB entityB) {
        return entityBRepository.findById(entityB.getId()).orElseThrow();
    }

    protected void assertPersistedEntityBToMatchAllProperties(EntityB expectedEntityB) {
        assertEntityBAllPropertiesEquals(expectedEntityB, getPersistedEntityB(expectedEntityB));
    }

    protected void assertPersistedEntityBToMatchUpdatableProperties(EntityB expectedEntityB) {
        assertEntityBAllUpdatablePropertiesEquals(expectedEntityB, getPersistedEntityB(expectedEntityB));
    }
}

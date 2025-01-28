package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.EntityAAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.EntityA;
import com.mycompany.myapp.repository.EntityARepository;
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
 * Integration tests for the {@link EntityAResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EntityAResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/entity-as";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityARepository entityARepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEntityAMockMvc;

    private EntityA entityA;

    private EntityA insertedEntityA;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntityA createEntity() {
        return new EntityA().name(DEFAULT_NAME).title(DEFAULT_TITLE).description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntityA createUpdatedEntity() {
        return new EntityA().name(UPDATED_NAME).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        entityA = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEntityA != null) {
            entityARepository.delete(insertedEntityA);
            insertedEntityA = null;
        }
    }

    @Test
    @Transactional
    void createEntityA() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EntityA
        var returnedEntityA = om.readValue(
            restEntityAMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityA)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EntityA.class
        );

        // Validate the EntityA in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEntityAUpdatableFieldsEquals(returnedEntityA, getPersistedEntityA(returnedEntityA));

        insertedEntityA = returnedEntityA;
    }

    @Test
    @Transactional
    void createEntityAWithExistingId() throws Exception {
        // Create the EntityA with an existing ID
        entityA.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEntityAMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityA)))
            .andExpect(status().isBadRequest());

        // Validate the EntityA in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllEntityAS() throws Exception {
        // Initialize the database
        insertedEntityA = entityARepository.saveAndFlush(entityA);

        // Get all the entityAList
        restEntityAMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entityA.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getEntityA() throws Exception {
        // Initialize the database
        insertedEntityA = entityARepository.saveAndFlush(entityA);

        // Get the entityA
        restEntityAMockMvc
            .perform(get(ENTITY_API_URL_ID, entityA.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(entityA.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingEntityA() throws Exception {
        // Get the entityA
        restEntityAMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEntityA() throws Exception {
        // Initialize the database
        insertedEntityA = entityARepository.saveAndFlush(entityA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityA
        EntityA updatedEntityA = entityARepository.findById(entityA.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEntityA are not directly saved in db
        em.detach(updatedEntityA);
        updatedEntityA.name(UPDATED_NAME).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restEntityAMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEntityA.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEntityA))
            )
            .andExpect(status().isOk());

        // Validate the EntityA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEntityAToMatchAllProperties(updatedEntityA);
    }

    @Test
    @Transactional
    void putNonExistingEntityA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityA.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntityAMockMvc
            .perform(put(ENTITY_API_URL_ID, entityA.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityA)))
            .andExpect(status().isBadRequest());

        // Validate the EntityA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEntityA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityA.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityAMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entityA))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEntityA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityA.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityAMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityA)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EntityA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEntityAWithPatch() throws Exception {
        // Initialize the database
        insertedEntityA = entityARepository.saveAndFlush(entityA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityA using partial update
        EntityA partialUpdatedEntityA = new EntityA();
        partialUpdatedEntityA.setId(entityA.getId());

        partialUpdatedEntityA.name(UPDATED_NAME);

        restEntityAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntityA.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntityA))
            )
            .andExpect(status().isOk());

        // Validate the EntityA in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntityAUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEntityA, entityA), getPersistedEntityA(entityA));
    }

    @Test
    @Transactional
    void fullUpdateEntityAWithPatch() throws Exception {
        // Initialize the database
        insertedEntityA = entityARepository.saveAndFlush(entityA);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityA using partial update
        EntityA partialUpdatedEntityA = new EntityA();
        partialUpdatedEntityA.setId(entityA.getId());

        partialUpdatedEntityA.name(UPDATED_NAME).title(UPDATED_TITLE).description(UPDATED_DESCRIPTION);

        restEntityAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntityA.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntityA))
            )
            .andExpect(status().isOk());

        // Validate the EntityA in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntityAUpdatableFieldsEquals(partialUpdatedEntityA, getPersistedEntityA(partialUpdatedEntityA));
    }

    @Test
    @Transactional
    void patchNonExistingEntityA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityA.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntityAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, entityA.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(entityA))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEntityA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityA.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityAMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(entityA))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEntityA() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityA.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityAMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(entityA)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EntityA in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEntityA() throws Exception {
        // Initialize the database
        insertedEntityA = entityARepository.saveAndFlush(entityA);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the entityA
        restEntityAMockMvc
            .perform(delete(ENTITY_API_URL_ID, entityA.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return entityARepository.count();
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

    protected EntityA getPersistedEntityA(EntityA entityA) {
        return entityARepository.findById(entityA.getId()).orElseThrow();
    }

    protected void assertPersistedEntityAToMatchAllProperties(EntityA expectedEntityA) {
        assertEntityAAllPropertiesEquals(expectedEntityA, getPersistedEntityA(expectedEntityA));
    }

    protected void assertPersistedEntityAToMatchUpdatableProperties(EntityA expectedEntityA) {
        assertEntityAAllUpdatablePropertiesEquals(expectedEntityA, getPersistedEntityA(expectedEntityA));
    }
}

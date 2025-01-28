package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.EntityHistoryAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static com.mycompany.myapp.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.EntityHistory;
import com.mycompany.myapp.domain.enumeration.Action;
import com.mycompany.myapp.repository.EntityHistoryRepository;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Base64;
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
 * Integration tests for the {@link EntityHistoryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EntityHistoryResourceIT {

    private static final String DEFAULT_USER_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_USER_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_ENTITY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_ENTITY_ID = 1L;
    private static final Long UPDATED_ENTITY_ID = 2L;

    private static final Action DEFAULT_ACTION_TYPE = Action.CREATE;
    private static final Action UPDATED_ACTION_TYPE = Action.UPDATE;

    private static final byte[] DEFAULT_CONTENT = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_CONTENT = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_CONTENT_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_CONTENT_CONTENT_TYPE = "image/png";

    private static final ZonedDateTime DEFAULT_CREATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String ENTITY_API_URL = "/api/entity-histories";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EntityHistoryRepository entityHistoryRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEntityHistoryMockMvc;

    private EntityHistory entityHistory;

    private EntityHistory insertedEntityHistory;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntityHistory createEntity() {
        return new EntityHistory()
            .userLogin(DEFAULT_USER_LOGIN)
            .entityName(DEFAULT_ENTITY_NAME)
            .entityId(DEFAULT_ENTITY_ID)
            .actionType(DEFAULT_ACTION_TYPE)
            .content(DEFAULT_CONTENT)
            .contentContentType(DEFAULT_CONTENT_CONTENT_TYPE)
            .creationDate(DEFAULT_CREATION_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EntityHistory createUpdatedEntity() {
        return new EntityHistory()
            .userLogin(UPDATED_USER_LOGIN)
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .actionType(UPDATED_ACTION_TYPE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);
    }

    @BeforeEach
    public void initTest() {
        entityHistory = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEntityHistory != null) {
            entityHistoryRepository.delete(insertedEntityHistory);
            insertedEntityHistory = null;
        }
    }

    @Test
    @Transactional
    void createEntityHistory() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EntityHistory
        var returnedEntityHistory = om.readValue(
            restEntityHistoryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityHistory)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EntityHistory.class
        );

        // Validate the EntityHistory in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertEntityHistoryUpdatableFieldsEquals(returnedEntityHistory, getPersistedEntityHistory(returnedEntityHistory));

        insertedEntityHistory = returnedEntityHistory;
    }

    @Test
    @Transactional
    void createEntityHistoryWithExistingId() throws Exception {
        // Create the EntityHistory with an existing ID
        entityHistory.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEntityHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityHistory)))
            .andExpect(status().isBadRequest());

        // Validate the EntityHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkUserLoginIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        entityHistory.setUserLogin(null);

        // Create the EntityHistory, which fails.

        restEntityHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityHistory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEntityNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        entityHistory.setEntityName(null);

        // Create the EntityHistory, which fails.

        restEntityHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityHistory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEntityIdIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        entityHistory.setEntityId(null);

        // Create the EntityHistory, which fails.

        restEntityHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityHistory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreationDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        entityHistory.setCreationDate(null);

        // Create the EntityHistory, which fails.

        restEntityHistoryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityHistory)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEntityHistories() throws Exception {
        // Initialize the database
        insertedEntityHistory = entityHistoryRepository.saveAndFlush(entityHistory);

        // Get all the entityHistoryList
        restEntityHistoryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(entityHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].userLogin").value(hasItem(DEFAULT_USER_LOGIN)))
            .andExpect(jsonPath("$.[*].entityName").value(hasItem(DEFAULT_ENTITY_NAME)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].actionType").value(hasItem(DEFAULT_ACTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].contentContentType").value(hasItem(DEFAULT_CONTENT_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(Base64.getEncoder().encodeToString(DEFAULT_CONTENT))))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(sameInstant(DEFAULT_CREATION_DATE))));
    }

    @Test
    @Transactional
    void getEntityHistory() throws Exception {
        // Initialize the database
        insertedEntityHistory = entityHistoryRepository.saveAndFlush(entityHistory);

        // Get the entityHistory
        restEntityHistoryMockMvc
            .perform(get(ENTITY_API_URL_ID, entityHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(entityHistory.getId().intValue()))
            .andExpect(jsonPath("$.userLogin").value(DEFAULT_USER_LOGIN))
            .andExpect(jsonPath("$.entityName").value(DEFAULT_ENTITY_NAME))
            .andExpect(jsonPath("$.entityId").value(DEFAULT_ENTITY_ID.intValue()))
            .andExpect(jsonPath("$.actionType").value(DEFAULT_ACTION_TYPE.toString()))
            .andExpect(jsonPath("$.contentContentType").value(DEFAULT_CONTENT_CONTENT_TYPE))
            .andExpect(jsonPath("$.content").value(Base64.getEncoder().encodeToString(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.creationDate").value(sameInstant(DEFAULT_CREATION_DATE)));
    }

    @Test
    @Transactional
    void getNonExistingEntityHistory() throws Exception {
        // Get the entityHistory
        restEntityHistoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEntityHistory() throws Exception {
        // Initialize the database
        insertedEntityHistory = entityHistoryRepository.saveAndFlush(entityHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityHistory
        EntityHistory updatedEntityHistory = entityHistoryRepository.findById(entityHistory.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEntityHistory are not directly saved in db
        em.detach(updatedEntityHistory);
        updatedEntityHistory
            .userLogin(UPDATED_USER_LOGIN)
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .actionType(UPDATED_ACTION_TYPE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);

        restEntityHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedEntityHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedEntityHistory))
            )
            .andExpect(status().isOk());

        // Validate the EntityHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEntityHistoryToMatchAllProperties(updatedEntityHistory);
    }

    @Test
    @Transactional
    void putNonExistingEntityHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntityHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, entityHistory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entityHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEntityHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityHistoryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(entityHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEntityHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityHistoryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(entityHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EntityHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEntityHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedEntityHistory = entityHistoryRepository.saveAndFlush(entityHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityHistory using partial update
        EntityHistory partialUpdatedEntityHistory = new EntityHistory();
        partialUpdatedEntityHistory.setId(entityHistory.getId());

        partialUpdatedEntityHistory.actionType(UPDATED_ACTION_TYPE);

        restEntityHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntityHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntityHistory))
            )
            .andExpect(status().isOk());

        // Validate the EntityHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntityHistoryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEntityHistory, entityHistory),
            getPersistedEntityHistory(entityHistory)
        );
    }

    @Test
    @Transactional
    void fullUpdateEntityHistoryWithPatch() throws Exception {
        // Initialize the database
        insertedEntityHistory = entityHistoryRepository.saveAndFlush(entityHistory);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the entityHistory using partial update
        EntityHistory partialUpdatedEntityHistory = new EntityHistory();
        partialUpdatedEntityHistory.setId(entityHistory.getId());

        partialUpdatedEntityHistory
            .userLogin(UPDATED_USER_LOGIN)
            .entityName(UPDATED_ENTITY_NAME)
            .entityId(UPDATED_ENTITY_ID)
            .actionType(UPDATED_ACTION_TYPE)
            .content(UPDATED_CONTENT)
            .contentContentType(UPDATED_CONTENT_CONTENT_TYPE)
            .creationDate(UPDATED_CREATION_DATE);

        restEntityHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEntityHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEntityHistory))
            )
            .andExpect(status().isOk());

        // Validate the EntityHistory in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEntityHistoryUpdatableFieldsEquals(partialUpdatedEntityHistory, getPersistedEntityHistory(partialUpdatedEntityHistory));
    }

    @Test
    @Transactional
    void patchNonExistingEntityHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityHistory.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEntityHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, entityHistory.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(entityHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEntityHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityHistoryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(entityHistory))
            )
            .andExpect(status().isBadRequest());

        // Validate the EntityHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEntityHistory() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        entityHistory.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEntityHistoryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(entityHistory)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the EntityHistory in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEntityHistory() throws Exception {
        // Initialize the database
        insertedEntityHistory = entityHistoryRepository.saveAndFlush(entityHistory);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the entityHistory
        restEntityHistoryMockMvc
            .perform(delete(ENTITY_API_URL_ID, entityHistory.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return entityHistoryRepository.count();
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

    protected EntityHistory getPersistedEntityHistory(EntityHistory entityHistory) {
        return entityHistoryRepository.findById(entityHistory.getId()).orElseThrow();
    }

    protected void assertPersistedEntityHistoryToMatchAllProperties(EntityHistory expectedEntityHistory) {
        assertEntityHistoryAllPropertiesEquals(expectedEntityHistory, getPersistedEntityHistory(expectedEntityHistory));
    }

    protected void assertPersistedEntityHistoryToMatchUpdatableProperties(EntityHistory expectedEntityHistory) {
        assertEntityHistoryAllUpdatablePropertiesEquals(expectedEntityHistory, getPersistedEntityHistory(expectedEntityHistory));
    }
}

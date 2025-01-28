package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.EntityHistory;
import com.mycompany.myapp.repository.EntityHistoryRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.EntityHistory}.
 */
@RestController
@RequestMapping("/api/entity-histories")
@Transactional
public class EntityHistoryResource {

    private static final Logger LOG = LoggerFactory.getLogger(EntityHistoryResource.class);

    private static final String ENTITY_NAME = "entityHistory";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EntityHistoryRepository entityHistoryRepository;

    public EntityHistoryResource(EntityHistoryRepository entityHistoryRepository) {
        this.entityHistoryRepository = entityHistoryRepository;
    }

    /**
     * {@code POST  /entity-histories} : Create a new entityHistory.
     *
     * @param entityHistory the entityHistory to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new entityHistory, or with status {@code 400 (Bad Request)} if the entityHistory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EntityHistory> createEntityHistory(@Valid @RequestBody EntityHistory entityHistory) throws URISyntaxException {
        LOG.debug("REST request to save EntityHistory : {}", entityHistory);
        if (entityHistory.getId() != null) {
            throw new BadRequestAlertException("A new entityHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        entityHistory = entityHistoryRepository.save(entityHistory);
        return ResponseEntity.created(new URI("/api/entity-histories/" + entityHistory.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, entityHistory.getId().toString()))
            .body(entityHistory);
    }

    /**
     * {@code PUT  /entity-histories/:id} : Updates an existing entityHistory.
     *
     * @param id the id of the entityHistory to save.
     * @param entityHistory the entityHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entityHistory,
     * or with status {@code 400 (Bad Request)} if the entityHistory is not valid,
     * or with status {@code 500 (Internal Server Error)} if the entityHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityHistory> updateEntityHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EntityHistory entityHistory
    ) throws URISyntaxException {
        LOG.debug("REST request to update EntityHistory : {}, {}", id, entityHistory);
        if (entityHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entityHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!entityHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        entityHistory = entityHistoryRepository.save(entityHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, entityHistory.getId().toString()))
            .body(entityHistory);
    }

    /**
     * {@code PATCH  /entity-histories/:id} : Partial updates given fields of an existing entityHistory, field will ignore if it is null
     *
     * @param id the id of the entityHistory to save.
     * @param entityHistory the entityHistory to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entityHistory,
     * or with status {@code 400 (Bad Request)} if the entityHistory is not valid,
     * or with status {@code 404 (Not Found)} if the entityHistory is not found,
     * or with status {@code 500 (Internal Server Error)} if the entityHistory couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EntityHistory> partialUpdateEntityHistory(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EntityHistory entityHistory
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EntityHistory partially : {}, {}", id, entityHistory);
        if (entityHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entityHistory.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!entityHistoryRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EntityHistory> result = entityHistoryRepository
            .findById(entityHistory.getId())
            .map(existingEntityHistory -> {
                if (entityHistory.getUserLogin() != null) {
                    existingEntityHistory.setUserLogin(entityHistory.getUserLogin());
                }
                if (entityHistory.getEntityName() != null) {
                    existingEntityHistory.setEntityName(entityHistory.getEntityName());
                }
                if (entityHistory.getEntityId() != null) {
                    existingEntityHistory.setEntityId(entityHistory.getEntityId());
                }
                if (entityHistory.getActionType() != null) {
                    existingEntityHistory.setActionType(entityHistory.getActionType());
                }
                if (entityHistory.getContent() != null) {
                    existingEntityHistory.setContent(entityHistory.getContent());
                }
                if (entityHistory.getContentContentType() != null) {
                    existingEntityHistory.setContentContentType(entityHistory.getContentContentType());
                }
                if (entityHistory.getCreationDate() != null) {
                    existingEntityHistory.setCreationDate(entityHistory.getCreationDate());
                }

                return existingEntityHistory;
            })
            .map(entityHistoryRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, entityHistory.getId().toString())
        );
    }

    /**
     * {@code GET  /entity-histories} : get all the entityHistories.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entityHistories in body.
     */
    @GetMapping("")
    public List<EntityHistory> getAllEntityHistories() {
        LOG.debug("REST request to get all EntityHistories");
        return entityHistoryRepository.findAll();
    }

    /**
     * {@code GET  /entity-histories/:id} : get the "id" entityHistory.
     *
     * @param id the id of the entityHistory to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entityHistory, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityHistory> getEntityHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EntityHistory : {}", id);
        Optional<EntityHistory> entityHistory = entityHistoryRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(entityHistory);
    }

    /**
     * {@code DELETE  /entity-histories/:id} : delete the "id" entityHistory.
     *
     * @param id the id of the entityHistory to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntityHistory(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EntityHistory : {}", id);
        entityHistoryRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

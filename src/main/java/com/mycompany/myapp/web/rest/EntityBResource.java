package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.EntityB;
import com.mycompany.myapp.repository.EntityBRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.EntityB}.
 */
@RestController
@RequestMapping("/api/entity-bs")
@Transactional
public class EntityBResource {

    private static final Logger LOG = LoggerFactory.getLogger(EntityBResource.class);

    private static final String ENTITY_NAME = "entityB";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EntityBRepository entityBRepository;

    public EntityBResource(EntityBRepository entityBRepository) {
        this.entityBRepository = entityBRepository;
    }

    /**
     * {@code POST  /entity-bs} : Create a new entityB.
     *
     * @param entityB the entityB to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new entityB, or with status {@code 400 (Bad Request)} if the entityB has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EntityB> createEntityB(@RequestBody EntityB entityB) throws URISyntaxException {
        LOG.debug("REST request to save EntityB : {}", entityB);
        if (entityB.getId() != null) {
            throw new BadRequestAlertException("A new entityB cannot already have an ID", ENTITY_NAME, "idexists");
        }
        entityB = entityBRepository.save(entityB);
        return ResponseEntity.created(new URI("/api/entity-bs/" + entityB.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, entityB.getId().toString()))
            .body(entityB);
    }

    /**
     * {@code PUT  /entity-bs/:id} : Updates an existing entityB.
     *
     * @param id the id of the entityB to save.
     * @param entityB the entityB to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entityB,
     * or with status {@code 400 (Bad Request)} if the entityB is not valid,
     * or with status {@code 500 (Internal Server Error)} if the entityB couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityB> updateEntityB(@PathVariable(value = "id", required = false) final Long id, @RequestBody EntityB entityB)
        throws URISyntaxException {
        LOG.debug("REST request to update EntityB : {}, {}", id, entityB);
        if (entityB.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entityB.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!entityBRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        entityB = entityBRepository.save(entityB);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, entityB.getId().toString()))
            .body(entityB);
    }

    /**
     * {@code PATCH  /entity-bs/:id} : Partial updates given fields of an existing entityB, field will ignore if it is null
     *
     * @param id the id of the entityB to save.
     * @param entityB the entityB to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entityB,
     * or with status {@code 400 (Bad Request)} if the entityB is not valid,
     * or with status {@code 404 (Not Found)} if the entityB is not found,
     * or with status {@code 500 (Internal Server Error)} if the entityB couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EntityB> partialUpdateEntityB(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EntityB entityB
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EntityB partially : {}, {}", id, entityB);
        if (entityB.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entityB.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!entityBRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EntityB> result = entityBRepository
            .findById(entityB.getId())
            .map(existingEntityB -> {
                if (entityB.getFirstName() != null) {
                    existingEntityB.setFirstName(entityB.getFirstName());
                }
                if (entityB.getLastName() != null) {
                    existingEntityB.setLastName(entityB.getLastName());
                }

                return existingEntityB;
            })
            .map(entityBRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, entityB.getId().toString())
        );
    }

    /**
     * {@code GET  /entity-bs} : get all the entityBS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entityBS in body.
     */
    @GetMapping("")
    public List<EntityB> getAllEntityBS() {
        LOG.debug("REST request to get all EntityBS");
        return entityBRepository.findAll();
    }

    /**
     * {@code GET  /entity-bs/:id} : get the "id" entityB.
     *
     * @param id the id of the entityB to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entityB, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityB> getEntityB(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EntityB : {}", id);
        Optional<EntityB> entityB = entityBRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(entityB);
    }

    /**
     * {@code DELETE  /entity-bs/:id} : delete the "id" entityB.
     *
     * @param id the id of the entityB to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntityB(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EntityB : {}", id);
        entityBRepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

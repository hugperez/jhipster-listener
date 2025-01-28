package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.EntityA;
import com.mycompany.myapp.repository.EntityARepository;
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
 * REST controller for managing {@link com.mycompany.myapp.domain.EntityA}.
 */
@RestController
@RequestMapping("/api/entity-as")
@Transactional
public class EntityAResource {

    private static final Logger LOG = LoggerFactory.getLogger(EntityAResource.class);

    private static final String ENTITY_NAME = "entityA";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EntityARepository entityARepository;

    public EntityAResource(EntityARepository entityARepository) {
        this.entityARepository = entityARepository;
    }

    /**
     * {@code POST  /entity-as} : Create a new entityA.
     *
     * @param entityA the entityA to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new entityA, or with status {@code 400 (Bad Request)} if the entityA has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<EntityA> createEntityA(@RequestBody EntityA entityA) throws URISyntaxException {
        LOG.debug("REST request to save EntityA : {}", entityA);
        if (entityA.getId() != null) {
            throw new BadRequestAlertException("A new entityA cannot already have an ID", ENTITY_NAME, "idexists");
        }
        entityA = entityARepository.save(entityA);
        return ResponseEntity.created(new URI("/api/entity-as/" + entityA.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, entityA.getId().toString()))
            .body(entityA);
    }

    /**
     * {@code PUT  /entity-as/:id} : Updates an existing entityA.
     *
     * @param id the id of the entityA to save.
     * @param entityA the entityA to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entityA,
     * or with status {@code 400 (Bad Request)} if the entityA is not valid,
     * or with status {@code 500 (Internal Server Error)} if the entityA couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EntityA> updateEntityA(@PathVariable(value = "id", required = false) final Long id, @RequestBody EntityA entityA)
        throws URISyntaxException {
        LOG.debug("REST request to update EntityA : {}, {}", id, entityA);
        if (entityA.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entityA.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!entityARepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        entityA = entityARepository.save(entityA);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, entityA.getId().toString()))
            .body(entityA);
    }

    /**
     * {@code PATCH  /entity-as/:id} : Partial updates given fields of an existing entityA, field will ignore if it is null
     *
     * @param id the id of the entityA to save.
     * @param entityA the entityA to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated entityA,
     * or with status {@code 400 (Bad Request)} if the entityA is not valid,
     * or with status {@code 404 (Not Found)} if the entityA is not found,
     * or with status {@code 500 (Internal Server Error)} if the entityA couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<EntityA> partialUpdateEntityA(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody EntityA entityA
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EntityA partially : {}, {}", id, entityA);
        if (entityA.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, entityA.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!entityARepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<EntityA> result = entityARepository
            .findById(entityA.getId())
            .map(existingEntityA -> {
                if (entityA.getName() != null) {
                    existingEntityA.setName(entityA.getName());
                }
                if (entityA.getTitle() != null) {
                    existingEntityA.setTitle(entityA.getTitle());
                }
                if (entityA.getDescription() != null) {
                    existingEntityA.setDescription(entityA.getDescription());
                }

                return existingEntityA;
            })
            .map(entityARepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, entityA.getId().toString())
        );
    }

    /**
     * {@code GET  /entity-as} : get all the entityAS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of entityAS in body.
     */
    @GetMapping("")
    public List<EntityA> getAllEntityAS() {
        LOG.debug("REST request to get all EntityAS");
        return entityARepository.findAll();
    }

    /**
     * {@code GET  /entity-as/:id} : get the "id" entityA.
     *
     * @param id the id of the entityA to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the entityA, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityA> getEntityA(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EntityA : {}", id);
        Optional<EntityA> entityA = entityARepository.findById(id);
        return ResponseUtil.wrapOrNotFound(entityA);
    }

    /**
     * {@code DELETE  /entity-as/:id} : delete the "id" entityA.
     *
     * @param id the id of the entityA to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntityA(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EntityA : {}", id);
        entityARepository.deleteById(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}

package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.EntityB;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EntityB entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EntityBRepository extends JpaRepository<EntityB, Long> {}

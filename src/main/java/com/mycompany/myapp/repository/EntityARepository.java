package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.EntityA;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EntityA entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EntityARepository extends JpaRepository<EntityA, Long> {}

package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.EntityHistory;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the EntityHistory entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EntityHistoryRepository extends JpaRepository<EntityHistory, Long> {}

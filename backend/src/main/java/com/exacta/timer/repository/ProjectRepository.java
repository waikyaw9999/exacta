package com.exacta.timer.repository;

import com.exacta.timer.entity.Project;
import com.exacta.timer.entity.ProjectStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    boolean existsByClientId(Long clientId);

    @Query("SELECT p FROM Project p JOIN FETCH p.client WHERE p.id = :id")
    Optional<Project> findByIdWithClient(@Param("id") Long id);

    @Query("""
            SELECT p FROM Project p
            JOIN FETCH p.client
            WHERE (:status IS NULL OR p.status = :status)
              AND (:clientId IS NULL OR p.client.id = :clientId)
            ORDER BY p.name
            """)
    List<Project> search(@Param("clientId") Long clientId, @Param("status") ProjectStatus status);
}

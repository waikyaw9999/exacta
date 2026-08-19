package com.exacta.timer.repository;

import com.exacta.timer.entity.TimeEntry;
import com.exacta.timer.entity.TimeEntryStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {

    boolean existsByProjectId(Long projectId);

    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndStatus(Long userId, TimeEntryStatus status);

    @Query("""
            SELECT t FROM TimeEntry t
            JOIN FETCH t.user
            JOIN FETCH t.project p
            JOIN FETCH p.client
            WHERE t.id = :id
            """)
    Optional<TimeEntry> findByIdWithRelations(@Param("id") Long id);

    @Query("""
            SELECT t FROM TimeEntry t
            JOIN FETCH t.user
            JOIN FETCH t.project p
            JOIN FETCH p.client
            WHERE t.user.id = :userId AND t.status = :status
            """)
    Optional<TimeEntry> findByUserIdAndStatusWithRelations(
            @Param("userId") Long userId, @Param("status") TimeEntryStatus status);

    @Query("""
            SELECT t FROM TimeEntry t
            JOIN FETCH t.user
            JOIN FETCH t.project p
            JOIN FETCH p.client
            WHERE (:userId IS NULL OR t.user.id = :userId)
              AND (:projectId IS NULL OR t.project.id = :projectId)
              AND (:status IS NULL OR t.status = :status)
            ORDER BY t.startTime DESC
            """)
    List<TimeEntry> search(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId,
            @Param("status") TimeEntryStatus status);
}

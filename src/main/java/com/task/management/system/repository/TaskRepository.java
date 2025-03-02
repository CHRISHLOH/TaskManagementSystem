package com.task.management.system.repository;

import com.task.management.system.enums.Status;
import com.task.management.system.model.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.author " +
            "LEFT JOIN FETCH t.assignee " +
            "WHERE t.id = :taskId")
    Task findTaskByIdWithUsers(@Param("taskId") Long taskId);

    @Modifying
    @Query("DELETE FROM Task t WHERE t.id = :taskId")
    void deleteTaskById(@Param("taskId") Long taskId);

    boolean existsByIdAndAuthor_Email(Long taskId, String email);
    boolean existsByIdAndAssignee_Email(Long taskId, String email);

    @Modifying
    @Query("UPDATE Task t SET t.status = :status WHERE t.id = :taskId")
    void updateTaskStatus(@Param("taskId") Long taskId, @Param("status") Status status);

    @EntityGraph(attributePaths = {"author", "assignee"})
    Page<Task> findAll(Specification<Task> spec, Pageable pageable);
}

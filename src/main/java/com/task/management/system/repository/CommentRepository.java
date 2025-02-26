package com.task.management.system.repository;

import com.task.management.system.model.entity.Comment;
import com.task.management.system.model.entity.CommentProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT c FROM Comment c
            LEFT JOIN FETCH c.author
            WHERE c.task.id = :taskId
            """)
    List<Comment> findCommentsByTaskIdWithAuthor(@Param("taskId") Long taskId);

    @Modifying
    @Query("""
            DELETE FROM Comment c WHERE c.task.id = :taskId
            """)
    void deleteCommentsByTaskId(@Param("taskId") Long taskId);

    @Query("""
        SELECT c FROM Comment c
        LEFT JOIN FETCH c.author
        WHERE c.id = :id
        """)
    Optional<Comment> findCommentById(@Param("id") Long id);

    @Query("""
            SELECT c FROM Comment c
            LEFT JOIN FETCH c.author
            WHERE c.author.email = :email
            """)
    List<Comment> findTaskByUserEmail(@Param("email") String email);

    @Query("""
    SELECT
        c.id AS id,
        c.content AS content,
        c.author.id AS authorId,
        c.author.email AS authorEmail,
        c.task.id AS taskId
    FROM Comment c
    LEFT JOIN c.task
    LEFT JOIN c.author
    WHERE c.task.id IN :taskIds
""")
    List<CommentProjection> findByTaskIdIn(@Param("taskIds") List<Long> taskIds);

}

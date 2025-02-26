package com.task.management.system.repository;

import com.task.management.system.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
        SELECT u
        FROM User u
        JOIN FETCH u.roles r
        WHERE u.email = :email
""")
    User findUserWithRolesByEmail(@Param("email") String email);

    User findByEmail (String email);
}

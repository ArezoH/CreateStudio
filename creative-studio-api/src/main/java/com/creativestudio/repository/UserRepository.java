package com.creativestudio.repository;

import com.creativestudio.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository gives us free methods: save(), findAll(), findById(), delete()
// We just add custom ones we need
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring auto-generates the SQL: SELECT * FROM users WHERE email = ?
    Optional<User> findByEmail(String email);

    // Spring auto-generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    boolean existsByEmail(String email);
}
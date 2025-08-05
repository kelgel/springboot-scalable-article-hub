package com.euni.articlehub.repository;

import com.euni.articlehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // SELECT * FROM user WHERE email = ?
    Optional<User> findByEmail(String email); //결과가 없을 수도 있으니 Optional

    // SELECT COUNT(*) > 0 FROM user WHERE email = ?
    boolean existsByEmail(String email);
}

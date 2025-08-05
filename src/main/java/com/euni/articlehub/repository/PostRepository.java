package com.euni.articlehub.repository;

import com.euni.articlehub.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    //List<Post> findAllByIsDeletedFalse();
    Page<Post> findAllByIsDeletedFalse(Pageable pageable);
}

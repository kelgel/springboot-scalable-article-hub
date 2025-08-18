package com.euni.articlehub.repository;

import com.euni.articlehub.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(Long userId);
    //List<Post> findAllByIsDeletedFalse();
    Page<Post> findAllByIsDeletedFalse(Pageable pageable);
    Page<Post> findByTitleContainingOrContentContaining(
            String title,
            String content,
            Pageable pageable
    );

    @Modifying
    @Transactional
    @Query("update Post p set p.views = p.views + :delta where p.id = :postId")
    void incrementViews(Long postId, int delta);
}

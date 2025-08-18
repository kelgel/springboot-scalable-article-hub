package com.euni.articlehub.service;
// 글 작성, 수정, 삭제, 목록 조회 등

import com.euni.articlehub.dto.PostRequestDto;
import com.euni.articlehub.dto.PostResponseDto;
import com.euni.articlehub.entity.Post;
import com.euni.articlehub.entity.User;
import com.euni.articlehub.repository.PostRepository;
import com.euni.articlehub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PopularityService popularityService;


    //사용자 ID 기반으로 새 글 작성
    @Transactional
    @CacheEvict(cacheNames = {"search:mysql", "search:es"}, allEntries = true) // 검색 캐시 전부 비우기
    public Post createPost(Long userId, PostRequestDto dto) {
        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .user(user)
                .build();

        return postRepository.save(post);
    }

//    public List<PostResponseDto> getAllPosts() {
//        // 1. postRepository.findAll() 또는 isDeleted-false 조건
//        List<Post> posts = postRepository.findAllByIsDeletedFalse();
//        // 2. Post -> PostResponseDto 변환해서 반환
//        return posts.stream()
//                .map(post -> PostResponseDto.from(post))
//                .collect(Collectors.toList());
//    }

    @Transactional(readOnly = true)
    public Page<PostResponseDto> getPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllByIsDeletedFalse(pageable);
        return posts.map(post -> PostResponseDto.from(post));
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long id, String viewerKey) {
        // 1. id로 게시글 조회
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

            // 조회수/랭킹 증가 (예외 흐름과 분리: 예외 터져도 서비스 영향 최소)
            try {
                popularityService.recordView(id, viewerKey);
            } catch (Exception ignore) { /* 로깅만 하고 넘어가도 OK */ }

        // 2. 없으면 예외, 있으면 DTO로 변환 후 반환
        return PostResponseDto.from(post);
    }

    // PostSearchService 또는 별도 ReadService에 추가: ID로 게시글 요약 조회
    public List<PostResponseDto> getTopPosts(int limit) {
        List<Long> ids = popularityService.topPostIds(limit);
        if (ids.isEmpty()) return List.of();

        // DB에서 id 목록으로 조회 후, 입력 순서대로 정렬
        List<Post> posts = postRepository.findAllById(ids);
        Map<Long, Post> byId = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> p));
        return ids.stream()
                .map(byId::get)
                .filter(p -> p != null && !Boolean.TRUE.equals(p.getIsDeleted()))
                .map(PostResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(cacheNames = {"search:mysql", "search:es"}, allEntries = true)
    public void updatePost(Long postId, PostRequestDto dto, Long userId) {
        // 1. 게시글 존재 여부 + 작성자 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        // 2. 내용 수정 후 저장
        if(!(post.getUser().getId().equals(userId))) throw new RuntimeException("Not allowed");

        post.update(dto.getTitle(), dto.getContent());
    }

    @Transactional
    @CacheEvict(cacheNames = {"search:mysql", "search:es"}, allEntries = true)
    public void deletePost(Long postId, Long userId) {
        // 1. 게시글 존재 여부 + 작성자 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 2. isDeleted-true 처리 (소프트 삭제)
        if(!(post.getUser().getId().equals(userId))) throw new RuntimeException("Not allowed");

        post.setIsDeleted(true); //Soft delete
    }
}

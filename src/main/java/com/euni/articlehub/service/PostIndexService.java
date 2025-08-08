package com.euni.articlehub.service;

import com.euni.articlehub.document.PostDocument;
import com.euni.articlehub.entity.Post;
import com.euni.articlehub.repository.PostDocumentRepository;
import com.euni.articlehub.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostIndexService {
    //이미 MySQL에 있는 게시글 데이터를 ES에 “색인(Indexing)”하는 과정

    private final PostRepository postRepository;
    private final PostDocumentRepository postDocumentRepository;

    public void indexAllPosts() {
        List<Post> posts = postRepository.findAll();

        List<PostDocument> documents = posts.stream()
                .map(post -> PostDocument.builder()
                        .id(post.getId().toString())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .author(post.getUser().getNickname())
                        .regDate(post.getRegDate().toString())
                        .build()
                ).toList();

        postDocumentRepository.saveAll(documents);
        System.out.println("All posts indexed in Elasticsearch.");
    }
}

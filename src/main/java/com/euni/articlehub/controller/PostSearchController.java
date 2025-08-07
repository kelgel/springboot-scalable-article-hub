package com.euni.articlehub.controller;

import com.euni.articlehub.dto.PostResponseDto;
import com.euni.articlehub.dto.PostSearchRequestDto;
import com.euni.articlehub.service.PostSearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostSearchController {

    private final PostSearchService postSearchService;

    @GetMapping("/mysql")
    @Operation(summary = "Search posts using MySQL", description = "Searches for posts using MySQL LIKE query")
    public List<PostResponseDto> searchPostsWithMySQL(PostSearchRequestDto requestDto) {
        return postSearchService.searchByMySQL(requestDto);
    }

    @GetMapping("/es")
    @Operation(summary = "Search posts using Elasticsearch", description = "Searches for posts using Elasticsearch fulltext query")
    public List<PostResponseDto> searchPostsWithElasticsearch(PostSearchRequestDto requestDto) {
        return postSearchService.searchByElasticsearch(requestDto);
    }



}

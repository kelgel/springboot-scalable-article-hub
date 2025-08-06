package com.euni.articlehub.controller;

import com.euni.articlehub.document.PostDocument;
import com.euni.articlehub.repository.PostDocumentRepository;
import com.euni.articlehub.repository.PostRepository;
import com.euni.articlehub.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public Page<PostDocument> search(String keyword, Pageable pageable) {
        return searchService.searchByKeyword(keyword, pageable);
    }
}

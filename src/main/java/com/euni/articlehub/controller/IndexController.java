package com.euni.articlehub.controller;

import com.euni.articlehub.entity.Post;
import com.euni.articlehub.service.PostIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/index")
public class IndexController {

    private final PostIndexService postIndexService;

    @PostMapping
    public String indexPosts() {
        postIndexService.indexAllPosts();
        return "Index posts successfully";
    }
}

package com.euni.articlehub.service;

import com.euni.articlehub.document.PostDocument;
import com.euni.articlehub.repository.PostDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final PostDocumentRepository postDocumentRepository;

    public Page<PostDocument> searchByKeyword(String keyword, Pageable pageable) {
        return postDocumentRepository.findByTitleContainingOrContentContaining(keyword, keyword, pageable);
    }
}

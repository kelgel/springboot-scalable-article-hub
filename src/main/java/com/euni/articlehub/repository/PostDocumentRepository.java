package com.euni.articlehub.repository;

import com.euni.articlehub.document.PostDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PostDocumentRepository extends ElasticsearchRepository<PostDocument, String> {
    Page<PostDocument> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}

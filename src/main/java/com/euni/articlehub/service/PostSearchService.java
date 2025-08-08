package com.euni.articlehub.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.euni.articlehub.document.PostDocument;
import com.euni.articlehub.dto.PostResponseDto;
import com.euni.articlehub.dto.PostSearchRequestDto;
import com.euni.articlehub.entity.Post;
import com.euni.articlehub.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostSearchService {

    private final PostRepository postRepository;
    private final ElasticsearchClient elasticsearchClient;

    public List<PostResponseDto> searchByMySQL(PostSearchRequestDto requestDto) {
        // 1. 정렬 객체 생성
        Sort sort = Sort.by(
                Sort.Direction.fromString(requestDto.getSortDirection()),
                requestDto.getSortBy()
        );
        // 2. 페이징 객체 생성
        Pageable pageable = PageRequest.of(
                requestDto.getPage(),
                requestDto.getSize(),
                sort
        );
        // 3. JPA 메서드로 게시글 검색 (제목 또는 내용에 키워드 포함)
        Page<Post> page = postRepository.findByTitleContainingOrContentContaining(
                requestDto.getKeyword(),
                requestDto.getKeyword(),
                pageable
        );
        // 4. Entity → DTO 변환
        // 5. List<PostResponseDto> 반환
        return page.stream()
                .map(PostResponseDto::from)
                .collect(Collectors.toList());
    }

    public List<PostResponseDto> searchByElasticsearch(PostSearchRequestDto requestDto){
        // 1. 검색 조건에 따른 Query 객체 생성
//        MatchQuery titleMatch = new MatchQuery.Builder()
//                .field("title")
//                .query(requestDto.getKeyword())
//                .build();
//
//        MatchQuery contentMatch = new MatchQuery.Builder()
//                .field("content")
//                .query(requestDto.getKeyword())
//                .build();
//
//        Query titleQuery = new Query.Builder().match(titleMatch).build();
//        Query contentQuery = new Query.Builder().match(contentMatch).build();

        MultiMatchQuery multiMatch = new MultiMatchQuery.Builder()
                .fields("title", "content")
                .query(requestDto.getKeyword())
                .build();

        Query multiMatchQuery = new Query.Builder().multiMatch(multiMatch).build();

        //filter 조건 리스트 - 작성자, 날짜 범위
        List<Query> filters = new ArrayList<>();

        //author filter
        if(requestDto.getAuthor() != null && !requestDto.getAuthor().isBlank()) {
            TermQuery authorFilter = new TermQuery.Builder()
                    .field("author.keyword")
                    .value(requestDto.getAuthor())
                    .build();

            filters.add(new Query.Builder().term(authorFilter).build());
        }

        //regDate filter
        if(requestDto.getStartDate() != null && requestDto.getEndDate() != null) {
            RangeQuery dateRange = new RangeQuery.Builder()
                    .field("regDate")
                    .gte(JsonData.of(requestDto.getStartDate()))
                    .lte(JsonData.of(requestDto.getEndDate()))
                    .build();

            filters.add(new Query.Builder().range(dateRange).build());
        }

        // AND 조건: title + content 모두 검색
        BoolQuery boolQuery = new BoolQuery.Builder()
                //.must(List.of(titleQuery, contentQuery))
                .must(multiMatchQuery)
                .filter(filters)
                .build();

        Query finalQuery = new Query.Builder().bool(boolQuery).build();

        //sorting Options
        final String sortField;
        String sortBy = requestDto.getSortBy();
        if ("author".equals(sortBy)) {
            sortField = "author.keyword";
        } else {
            sortField = sortBy;
        }

        SortOptions sortOptions = new SortOptions.Builder()
                .field(f -> f
                        .field(sortField)
                        .order(requestDto.getSortDirection().equalsIgnoreCase("DESC") ? SortOrder.Desc : SortOrder.Asc)
                )
                .build();

        // search
        SearchResponse<PostDocument> response;
        try {
            response = elasticsearchClient.search(
                    s -> s
                            .index("posts")
                            .query(finalQuery)
                            .from(requestDto.getPage() * requestDto.getSize()) //page=2, size=10 → from=20
                            .size(requestDto.getSize())
                            .sort(sortOptions),
                    PostDocument.class
            );
        } catch (IOException e) {
            throw new RuntimeException("Elasticsearch search error: " + e.getMessage());
        }
        System.out.println(response.toString());

        //result -SearchResponse<PostDocument>	→ .map() 으로 DTO 변환	→ List<PostResponseDto>
        return  response.hits().hits().stream() //문서 리스트 가져오기 - List<SearchHit<PostDocument>>)
                .map(hit -> PostResponseDto.fromDocument(hit.source()))
                .collect(Collectors.toList());
    }
}

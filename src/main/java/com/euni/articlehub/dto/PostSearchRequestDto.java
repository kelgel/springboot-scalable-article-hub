package com.euni.articlehub.dto;

import lombok.*;

@Getter
@Setter
public class PostSearchRequestDto {
    //검색 조건을 담을 DTO 생성

    private String keyword;
    private String author;
    private String startDate;
    private String endDate;
    private String sortBy = "regDate";
    private String sortDirection = "DESC";
    private int page = 0;
    private int size = 10;
}

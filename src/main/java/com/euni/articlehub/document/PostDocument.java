package com.euni.articlehub.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "posts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PostDocument {

    @Id
    private String id;
    private String title;
    private String content;
    private String author;
    //@Field(type = FieldType.Date, format = {}, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private String regDate;
}

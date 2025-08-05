package com.euni.articlehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class PostRequestDto {
    @Schema(description = "Title of the post", example = "Introduction to JWT")
    private String title;

    @Schema(description = "Content/body of the post", example = "JWT stands for JSON Web Token...")
    private String content;
}

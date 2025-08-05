package com.euni.articlehub.dto;

import com.euni.articlehub.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostResponseDto {
    @Schema(description = "Unique identifier of the post", example = "1")
    private Long id;

    @Schema(description = "Title of the post", example = "Understanding JWT Flow")
    private String title;

    @Schema(description = "Content/body of the post", example = "In this post, we will explore the JWT authentication flow...")
    private String content;

    @Schema(description = "Number of times the post has been viewed", example = "57")
    private int views;

    @Schema(description = "Nickname of the user who wrote the post", example = "devUser")
    private String nickname;

    @Schema(description = "Registration date of the post", example = "2025-08-05 10:30:00")
    private LocalDateTime regDate;


    // Post 엔티티를 PostResponseDto로 변환하는 정적 팩토리 메서드
    public static PostResponseDto from(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setViews(post.getViews());
        dto.setNickname(post.getUser().getNickname());
        dto.setRegDate(post.getRegDate());
        return dto;
    }
}




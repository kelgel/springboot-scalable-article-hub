package com.euni.articlehub.dto;

import com.euni.articlehub.document.PostDocument;
import com.euni.articlehub.entity.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public static PostResponseDto fromDocument(PostDocument doc) {
        LocalDateTime parsed = null;
        String s = doc.getRegDate();
        if (s != null) {
            try {
                // Z(UTC) 또는 오프셋이 있을 때
                parsed = OffsetDateTime.parse(s).toLocalDateTime();
            } catch (Exception e1) {
                try {
                    // 오프셋이 없고 소수점 초가 있는 ISO도 LocalDateTime.parse가 처리 가능
                    parsed = LocalDateTime.parse(s);
                } catch (Exception e2) {
                    // 필요시 포맷터 추가 시도 or null 유지
                    parsed = null;
                }
            }
        }

        return PostResponseDto.builder()
                .id(Long.parseLong(doc.getId()))
                .title(doc.getTitle())
                .content(doc.getContent())
                .nickname(doc.getAuthor())
                .regDate(parsed)
                .build();
    }
}
package com.euni.articlehub.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) //FK
    private User user;

    private String title;

    private String content;

    @Builder.Default
    @Column(nullable = false)
    private Integer views = 0;

    @CreatedDate
    private LocalDateTime regDate;

    @LastModifiedDate
    private LocalDateTime modDate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        this.modDate = LocalDateTime.now();
    }
}

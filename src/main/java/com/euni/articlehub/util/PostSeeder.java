package com.euni.articlehub.util;

import com.euni.articlehub.dto.PostResponseDto;
import com.euni.articlehub.entity.Post;
import com.euni.articlehub.entity.User;
import com.euni.articlehub.repository.PostRepository;
import com.euni.articlehub.repository.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class PostSeeder implements CommandLineRunner {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final Faker faker = new Faker(new Locale("en"));
    private final Random random = new Random();

    private final String[] keywords = {
            "hope", "loss", "freedom", "justice", "revenge",
            "love", "betrayal", "healing", "war", "faith"
    };

    @Override
    @Transactional
    public void run(String... args) {
        if (postRepository.count() >= 10000) {
            System.out.println("✅ 이미 게시글 1만 건 이상 존재함. 시드 작업 생략.");
            return;
        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.err.println("❌ 사용자 데이터가 없습니다. 최소 1명 이상의 유저를 생성해주세요.");
            return;
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            User user = users.get(random.nextInt(users.size()));
            String theme1 = keywords[random.nextInt(keywords.length)];
            String theme2 = keywords[random.nextInt(keywords.length)];

            String content = String.format(
                    "This story explores the themes of %s and %s. " +
                            "It follows deeply emotional characters through unexpected twists and powerful resolutions.",
                    theme1, theme2
            );

            Post post = Post.builder()
                    .user(user)
                    .title(faker.book().title())
                    .content(content)
                    .views(random.nextInt(500))
                    .isDeleted(false)
                    .build();

            posts.add(post);

            // batch insert 성능 위해 일정 단위 저장
            if (posts.size() == 1000) {
                postRepository.saveAll(posts);
                posts.clear();
                System.out.println("📌 1000건 저장 완료...");
            }
        }
        if (!posts.isEmpty()) {
            postRepository.saveAll(posts);
        }
        stopWatch.stop();
        System.out.println("✅ 10,000건 게시글 생성 완료!");
        System.out.println("⏱ 총 소요 시간: " + stopWatch.getTotalTimeMillis() + " ms");
    }
}

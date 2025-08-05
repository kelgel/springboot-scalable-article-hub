package com.euni.articlehub;

import com.euni.articlehub.entity.Post;
import com.euni.articlehub.entity.User;
import com.euni.articlehub.repository.PostRepository;
import com.euni.articlehub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.config.name=application")
class PostRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    public void InsertingPostTest() {
        // 1. 먼저 User 저장
        User user = new User();
        user.setNickname("test_user");
        user.setEmail("test@example.com");
        user.setPassword("securepassword");
        user.setRole("USER");
        User savedUser = userRepository.save(user);

        // 2. Post 생성 후 User 세팅
        Post post = new Post();
        post.setUser(savedUser); // 여기가 중요!
        post.setTitle("Test Title");
        post.setContent("This is the content.");
        post.setViews(0);
        post.setIsDeleted(false);

        // 3. Post 저장
        postRepository.save(post);
    }


}
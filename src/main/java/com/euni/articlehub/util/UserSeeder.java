package com.euni.articlehub.util;

import com.euni.articlehub.entity.User;
import com.euni.articlehub.repository.UserRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker();

    @Override
    public void run(String... args) {
        if(userRepository.count() >= 5) return;

        List<User> users = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            User user = User.builder()
                    .email(faker.internet().emailAddress())
                    .password(passwordEncoder.encode("password123"))
                    .nickname(faker.name().username())
                    .role("USER")
                    .build();
            users.add(user);
        }
        userRepository.saveAll(users);
        System.out.println("✅ 테스트 유저 5명 생성 완료");
    }

}

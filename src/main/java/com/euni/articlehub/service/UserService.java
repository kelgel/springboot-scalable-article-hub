package com.euni.articlehub.service;

import com.euni.articlehub.dto.LoginRequestDto;
import com.euni.articlehub.dto.SignupRequestDto;
import com.euni.articlehub.entity.User;
import com.euni.articlehub.repository.UserRepository;
import com.euni.articlehub.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// 회원가입, 로그인, 사용자 조회
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void signup(SignupRequestDto dto) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Already exists");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        //  DTO → Entity 변환
        User user = User.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .role("USER")
                .build();
        // 저장
        userRepository.save(user);
    }

    public String login(LoginRequestDto dto) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid login info"));
        // 2. 비밀번호 일치 확인 → passwordEncoder.matches(입력값, 저장된값)
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword()))
            throw new IllegalArgumentException("Invalid login info");
        // 3. 일치하면 JWT 토큰 생성 → JwtUtil.createToken(user)
        return jwtUtil.createToken(user.getId());
    }

    public User getUser(Long userId) {
        // findById로 조회
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user"));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}

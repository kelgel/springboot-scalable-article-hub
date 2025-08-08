package com.euni.articlehub.filter;
//요청이 들어올 때마다 JWT 토큰을 꺼내서 검증하고, 로그인한 사용자 정보를 등록하는 아주 중요한 역할

import com.euni.articlehub.entity.User;
import com.euni.articlehub.service.UserService;
import com.euni.articlehub.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

//@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    //private final UserService userService;
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected  void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
        throws ServletException, IOException {

        String uri = request.getRequestURI();
        System.out.println("Requested URI: " + uri);

        // '/api/posts/search'로 시작하는 요청은 토큰 검증 없이 통과
        if (uri.startsWith("/api/posts/search")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 요청 헤더에서 JWT  꺼내기
        String token = resolveToken(request);

        if (token != null && jwtUtil.validateToken(token)) {
            Long userId = jwtUtil.getUserIdFromToken(token);

            System.out.println("userId: " + userId);
            // Spring 기본 UserDetails 사용 (username = userId)
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    userId.toString(),
                    "",  // password는 필요 없음
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))  // ← 권한을 명시적으로 설정
            );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            // 핵심 코드 추가!
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    //요청 헤더에서 Bearer 토큰 추출하는 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); //Bearer 다음 문자열
        }
        return null;
    }
}

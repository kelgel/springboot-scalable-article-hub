package com.euni.articlehub.controller;

import com.euni.articlehub.dto.PostRequestDto;
import com.euni.articlehub.dto.PostResponseDto;
import com.euni.articlehub.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/posts") // 기본 URL 지정
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "Get all posts",
            description = "Retrieve a list of all posts",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved post list"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts() {
        List<PostResponseDto> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts); // status 200 OK + 목록 데이터 반환
    }

    @Operation(
            summary = "Get a single post",
            description = "Retrieve a single post by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved post"),
                    @ApiResponse(responseCode = "404", description = "Post not found"),
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDto> getPostById(@PathVariable Long id) {
        PostResponseDto post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @Operation(
            summary = "Create a new post",
            description = "Create a new post with title and content. Requires authentication.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully created post"),
                    @ApiResponse(responseCode = "400", description = "Invalid posts data"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Token is missing or invalid")
            }
    )
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostRequestDto dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        // Extract user ID
        Long userId = Long.parseLong(userDetails.getUsername());
        postService.createPost(userId, dto);
        return ResponseEntity.ok("Post successfully created");
    }

    @Operation(
            summary = "Delete a post",
            description = "Delete a post by ID. Only the author can delete their own post. Requires authentication.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully deleted post"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not the post owner"),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        postService.deletePost(id, userId);
        return ResponseEntity.ok("Post successfully deleted");
    }

    @Operation(
            summary = "Update a post",
            description = "Update title and content of  an existing post by ID. Only the author can update their own post. Requires authentication.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully updated post"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - Not the post owner"),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(@PathVariable Long id,
                                        @RequestBody PostRequestDto dto,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        postService.updatePost(id, dto, userId);
        return ResponseEntity.ok("Post successfully updated");
    }
}

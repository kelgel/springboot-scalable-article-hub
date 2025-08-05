package com.euni.articlehub.controller;

import com.euni.articlehub.dto.LoginRequestDto;
import com.euni.articlehub.dto.SignupRequestDto;
import com.euni.articlehub.entity.User;
import com.euni.articlehub.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "sign up",
            description = "Register a new user with email, password, and nickname.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully registered user"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "409", description = "User already exists")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequestDto dto) {
        userService.signup(dto);
        return ResponseEntity.ok("User successfully signed up");
    }

    @Operation(
            summary = "login",
            description = "Login with email and password.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully logged in"),
                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                    @ApiResponse(responseCode = "401", description = "Invalid login info")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto dto){
        String token = userService.login(dto);
        return ResponseEntity.ok(token);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieve user information by ID. Only the user themselves can access their own information.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – You are not authorized to access this user info"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        String tokenUserId = userDetails.getUsername();
        System.out.println("tokenUserId: " + tokenUserId);
        System.out.println("paramId: " + id);

        if (!tokenUserId.equals(id.toString())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Get my user info",
            description = "Retrieve the currently authenticated user's own information using token data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User information retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized – Token is missing or invalid")
            }
    )
    @GetMapping("/me")
    public ResponseEntity<User> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }
}

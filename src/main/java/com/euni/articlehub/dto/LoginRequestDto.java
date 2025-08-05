package com.euni.articlehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
public class LoginRequestDto {
    @Schema(description = "User's email address used for login", example = "user@example.com")
    private String email;

    @Schema(description = "User's password used for login", example = "password123")
    private String password;
}

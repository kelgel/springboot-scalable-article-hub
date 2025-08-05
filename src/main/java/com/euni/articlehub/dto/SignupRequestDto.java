package com.euni.articlehub.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
public class SignupRequestDto {
    @Schema(description = "User's email address", example = "test@example.com", required = true)
    private String email;

    @Schema(description = "User's password", example = "P@ssw0rd123", required = true)
    private String password;

    @Schema(description = "User's nickname", example = "euni", required = true)
    private String nickname;
}

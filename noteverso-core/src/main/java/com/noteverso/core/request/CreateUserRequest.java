package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;

@Getter
@Setter
public class CreateUserRequest {
    @NotBlank
    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email(message = "Invalid email address")
    private String email;

    @NotBlank
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @Length(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String captchaCode;
}

package com.noteverso.core.controller;

import cn.hutool.core.util.RandomUtil;
import com.noteverso.common.api.ApiResult;
import com.noteverso.common.exceptions.BusinessException;
import com.noteverso.common.exceptions.DuplicateRecordException;
import com.noteverso.core.request.CreateUserRequest;
import com.noteverso.core.request.LoginRequest;
import com.noteverso.core.response.LoginResponse;
import com.noteverso.core.security.jwt.JwtUtils;
import com.noteverso.core.service.EmailService;
import com.noteverso.core.service.UserService;
import com.noteverso.core.util.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.noteverso.core.constant.CacheConstants.*;
import static com.noteverso.core.constant.NumConstants.*;

@Tag(name = "Auth", description = "Auth management APIs")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final RedisUtils redisUtils;
    private final EmailService emailService;

    @Operation(description = "Login with username and password", tags = {"POST"})
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtUtils.generateJwtToken(authentication);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setUsername(loginRequest.getUsername());
        loginResponse.setToken(token);
        return loginResponse;
    }

    @Operation(description = "Sign up with username and password", tags = {"POST"})
    @PostMapping("/signup")
    public String signup(@RequestBody @Valid CreateUserRequest createUserRequest) {
        String email = createUserRequest.getEmail();

        if (userService.existsByEmail(email)) {
            throw new DuplicateRecordException("Email already exists");
        }

        Object captchaCode = redisUtils.get(String.format(CACHE_VERIFICATION_CAPTCHA_CODE, email));
        if (!createUserRequest.getCaptchaCode().equals(captchaCode)) {
            throw new BusinessException("Invalid captcha code");
        } else {
            redisUtils.del(String.format(CACHE_VERIFICATION_CAPTCHA_CODE, email));
        }

        userService.createUser(createUserRequest.getEmail(), createUserRequest.getUsername(), createUserRequest.getPassword());
        return "User created successfully";
    }

    @Operation(description = "verify captcha", tags = {"Get"})
    @GetMapping("/verify-captcha")
    public String getCaptcha(String email) {
        if (userService.existsByEmail(email)) {
            throw new DuplicateRecordException("Email already exists");
        }

        String captchaCreateTime = redisUtils.get(String.format(CACHE_VERIFICATION_CAPTCHA_CODE_CREATE_TIME, email));
        if (captchaCreateTime != null && System.currentTimeMillis() - Long.parseLong(captchaCreateTime) < CAPTCHA_INTERVAL) {
            // 验证码1分钟内只能发送一次
            throw new BusinessException("Please try again in 1 minute");
        }

        String sendTimes = redisUtils.get(String.format(CACHE_VERIFICATION_CAPTCHA_CODE_SEND_TIMES, email));
        if (sendTimes != null && Integer.parseInt(sendTimes) >= CAPTCHA_SEND_TIMES) {
            // 同邮箱注册验证码发送超过限制次数，直接进黑名单
            throw new BusinessException("Something went wrong, please try again later");
        }

        String totals = redisUtils.get(CACHE_CAPTCHA_CODE_SEND_TIMES_TOTAL);
        if (totals != null && Integer.parseInt(totals) >= CAPTCHA_SEND_TIMES_TOTAL) {
            // 邮箱验证码发送超过限制次数，明天再来
            throw new BusinessException("Too many requests, please try again tomorrow");
        }

        redisUtils.incr(String.format(CACHE_VERIFICATION_CAPTCHA_CODE_SEND_TIMES, email));
        if (!redisUtils.hasKey(CACHE_CAPTCHA_CODE_SEND_TIMES_TOTAL)) {
            redisUtils.set(CACHE_CAPTCHA_CODE_SEND_TIMES_TOTAL, "1", 24 * 60 * 60);
        } else {
            redisUtils.incr(CACHE_CAPTCHA_CODE_SEND_TIMES_TOTAL);
        }

        String captcha = String.valueOf(RandomUtil.randomInt(100000, 999999));
        redisUtils.set(String.format(CACHE_VERIFICATION_CAPTCHA_CODE, email), captcha, CAPTCHA_EXPIRE);
        redisUtils.set(String.format(CACHE_VERIFICATION_CAPTCHA_CODE_CREATE_TIME, email), String.valueOf(System.currentTimeMillis()));
        emailService.sendSimpleMessage(email, "Noteverso verification code", "Your verification code: " + captcha);

        return "Captcha sent successfully";
    }

}

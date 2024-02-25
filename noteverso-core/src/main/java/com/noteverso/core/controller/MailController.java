package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.core.request.MailRequest;
import com.noteverso.core.request.RedisRequest;
import com.noteverso.core.service.EmailService;
import com.noteverso.core.util.RedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Mail", description = "Mail management APIs")
@RestController
@RequestMapping("/api/v1/mails")
@AllArgsConstructor
@Slf4j
public class MailController {
    private final EmailService emailService;
    private final RedisUtils redisUtils;
    private final RedisTemplate redisTemplate;

    @Operation(summary = "Send mail", description = "Send mail", tags = { "POST" })
    @PostMapping("")
    public ApiResult<Void> sendMail(@Valid @RequestBody MailRequest request) {
        emailService.sendSimpleMessage(request.getTo(), request.getSubject(), request.getBody());
        return ApiResult.success(null);
    }

    @Operation(summary = "Send html mail", description = "Send html mail", tags = { "POST" })
    @PostMapping("/html")
    public ApiResult<Void> sendHtmlMail(@Valid @RequestBody MailRequest request) {
        emailService.sendHtmlMessage(request.getTo(), request.getSubject(), request.getBody());
        return ApiResult.success(null);
    }

    @Operation(summary = "Send attachment mail", description = "Send attachment mail", tags = {"POST"})
    @PostMapping("/attachment")
    public ApiResult<Void> sendAttachmentMail(@Valid @RequestBody MailRequest request) {
        emailService.sendMailWithAttachment(request.getTo(), request.getSubject(), request.getBody(), request.getFilePath());
        return ApiResult.success(null);
    }

    @Operation(summary = "Send Inline mail", description = "Send Inline mail", tags = {"POST"})
    @PostMapping("/inline")
    public ApiResult<Void> sendInlineMail(@Valid @RequestBody MailRequest request) {
        emailService.sendMailWithInlineResources(request.getTo(), request.getSubject(), request.getBody(), request.getFilePath());
        return ApiResult.success(null);
    }

    @Operation(summary = "Redis test", description = "Redis test", tags = {"POST"})
    @PostMapping("/redis")
    public ApiResult<Void> redisSaveTest(@Valid @RequestBody RedisRequest request) {
        redisTemplate.opsForValue().set(request.getKey(), request.getValue());
        return ApiResult.success(null);
    }

    @Operation(summary = "Redis test", description = "Redis test - GET KEY", tags = {"GET"})
    @GetMapping("/redis")
    public ApiResult<Object> redisGetTest(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return ApiResult.success(value);
    }
}

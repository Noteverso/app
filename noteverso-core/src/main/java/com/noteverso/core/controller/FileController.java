package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.common.exceptions.BusinessException;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.core.dto.AttachmentDTO;
import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.manager.UserConfigManager;
import com.noteverso.core.model.UploadResult;
import com.noteverso.core.model.UserConfig;
import com.noteverso.core.request.AttachmentRequest;
import com.noteverso.core.response.UploadFileGetResponse;
import com.noteverso.core.response.UploadResponse;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.AttachmentService;
import com.noteverso.core.service.component.OssClient;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

import static com.noteverso.core.constant.ExceptionConstants.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/files")
public class FileController {
    private final OssClient ossClient;
    private final AuthManager authManager;
    private final UserConfigManager userConfigManager;
    private final AttachmentService attachmentService;

    @PostMapping("/upload")
    public UploadResponse upload(Authentication authentication, @RequestPart("file") MultipartFile file, @RequestPart("resourceType") String resourceType) throws IOException {
        UserDetailsImpl principal = authManager.getPrincipal(authentication);
        String userId = principal.getUserId();
        UserConfig userConfig = userConfigManager.getUserConfig(principal.getUserId());
        if (userConfig == null) {
            throw new NoSuchDataException(USER_NOT_FOUND);
        }

        long fileTotalSize = attachmentService.userAttachmentTotalSize(userId);
        if (file.getSize() > userConfig.getMaxFileSize()) {
            throw new BusinessException(FILE_SIZE_EXCEED);
        }
        if (fileTotalSize + file.getSize() > userConfig.getFilesSizeQuota()) {
            throw new BusinessException(FILE_SIZE_SUM_EXCEED);
        }

        String key = ossClient.getKey(userId, file.getOriginalFilename());
        UploadResult result = ossClient.upload(file.getInputStream(), key, file.getContentType());

        UploadResponse uploadResponse = new UploadResponse();
        uploadResponse.setName(Objects.requireNonNull(file.getOriginalFilename()));
        uploadResponse.setType(Objects.requireNonNull(file.getContentType()));
        uploadResponse.setSize(file.getSize());
        uploadResponse.setUrl(result.getFileName());
        uploadResponse.setResourceType(resourceType);

        return uploadResponse;
    }

    @PostMapping("/getUploadFileUrl")
    public UploadFileGetResponse getUploadFileUrl(Authentication authentication, @RequestBody AttachmentRequest request) {
        UserDetailsImpl principal = authManager.getPrincipal(authentication);
        String userId = principal.getUserId();
        Long contentLength = request.getContentLength();

        UserConfig userConfig = userConfigManager.getUserConfig(principal.getUserId());
        if (userConfig == null) {
            throw new NoSuchDataException(USER_NOT_FOUND);
        }

        if (contentLength > userConfig.getMaxFileSize()) {
            throw new BusinessException(FILE_SIZE_EXCEED);
        }

        long fileTotalSize = attachmentService.userAttachmentTotalSize(userId);
        if (fileTotalSize + contentLength > userConfig.getFilesSizeQuota()) {
            throw new BusinessException(FILE_SIZE_SUM_EXCEED);
        }

        String key = ossClient.getKey(userId, request.getName());
        String signedPutUrl = ossClient.createPresignedPutUrl(key, 60 * 2);

        UploadFileGetResponse response = new UploadFileGetResponse();
        response.setSignedPutUrl(signedPutUrl);
        response.setSignedGetUrl(ossClient.getPrivateUrl(key, 60 * 5));
        response.setAttachmentUrl(key);

        return response;
    }

    @Operation(description = "Create attachment", tags = {"POST"})
    @PostMapping("/attachments")
    public String saveAttachments(Authentication authentication, @RequestBody AttachmentRequest request) {
        UserDetailsImpl principal = authManager.getPrincipal(authentication);
        String userId = principal.getUserId();

        AttachmentDTO attachmentDTO = new AttachmentDTO();
        attachmentDTO.setName(Objects.requireNonNull(request.getName()));
        attachmentDTO.setType(Objects.requireNonNull(request.getContentType()));
        attachmentDTO.setSize(request.getContentLength());
        attachmentDTO.setUrl(request.getUrl());
        attachmentDTO.setResourceType(request.getResourceType());

        return attachmentService.createAttachment(attachmentDTO, userId);
    }


    @GetMapping("/{attachmentId}")
    public String getUrl(Authentication authentication, @PathVariable("attachmentId") String attachmentId) {
        UserDetailsImpl principal = authManager.getPrincipal(authentication);
        return attachmentService.getPreviewSignature(attachmentId, principal.getUserId());
    }
}

package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UploadFileGetRequest {
    @NotNull(message = "attachment name is required")
    @Schema(description = "Attachment name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 附件 MIME 类型，如 video/*, audio/*, image/*
     */
    @NotNull(message = "ContentType is required")
    @Schema(description = "Attachment MIME type", requiredMode = Schema.RequiredMode.REQUIRED, example = "image/jpeg, video/mp4")
    private String contentType;

    /**
     * 附件资源类型，image - 图片，file - 文件
     */
    @NotNull(message = "resourceType is required")
    @Schema(description = "Attachment resource type", allowableValues = "image, file", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resourceType;

    /**
     * 附件资源类型，image - 图片，file - 文件
     */
    @NotNull(message = "size is required")
    @Schema(description = "Attachment resource size", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long contentLength;
}

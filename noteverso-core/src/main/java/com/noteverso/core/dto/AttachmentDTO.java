package com.noteverso.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttachmentDTO {
    @NotNull(message = "attachment name is required")
    @Schema(description = "Attachment name", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /**
     * 附件 MIME 类型，如 video/*, audio/*, image/*
     */
    @NotNull(message = "type is required")
    @Schema(description = "Attachment MIME type", requiredMode = Schema.RequiredMode.REQUIRED, example = "image/jpeg, video/mp4")
    private String type;

    /**
     * 附件 OSS 链接
     */
    @NotNull(message = "url is required")
    @Schema(description = "Attachment OSS url", requiredMode = Schema.RequiredMode.REQUIRED)
    private String url;

    /**
     * 附件资源类型，image - 图片，file - 文件
     */
    @NotNull(message = "resourceType is required")
    @Schema(description = "Attachment resource type", allowableValues = "0 - image, 1 - file", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer resourceType;

    /**
     * 附件资源类型，image - 图片，file - 文件
     */
    @NotNull(message = "size is required")
    @Schema(description = "Attachment resource size", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long size;

    /**
     *
     * 附件id
     */
    @NotNull(message = "attachmentId is required")
    @Schema(description = "attachment id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String attachmentId;
}

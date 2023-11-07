package com.noteverso.attachment.dto;

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
    @Schema(description = "Attachment resource type", allowableValues = "image, file", requiredMode = Schema.RequiredMode.REQUIRED)
    private String resourceType;

    /**
     * 附件资源类型，image - 图片，file - 文件
     */
    @NotNull(message = "resourceSize is required")
    @Schema(description = "Attachment resource size", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long resourceSize;

    /**
     * 笔记id，如果附件属于项目或评论，则为 null
     */
    @Schema(description = "Note id")
    private String noteId;

    /**
     * 项目id，如果附件属于笔记或评论，则它为 null
     */
    @Schema(description = "Project id")
    private String projectId;

    /**
     * 评论id，如果附件属于笔记或项目，则它为 null
     */
    @Schema(description = "Comment id")
    private String commentId;
}

package com.noteverso.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AttachmentDTO {
    /**
     * 附件名称
     */
    private String name;

    /**
     * 附件 MIME 类型，如 video/*, audio/*, image/*
     */
    private String type;

    /**
     * 附件 OSS 链接
     */
    private String url;

    /**
     * 附件资源类型，image - 图片，file - 文件
     */
    private String resourceType;

    /**
     * 附件资源类型，image - 图片，file - 文件, bytes
     */
    private Long size;

    /**
     *
     * 附件id
     */
    private String attachmentId;
}

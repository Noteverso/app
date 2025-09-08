package com.noteverso.core.model.response;

import lombok.Data;

@Data
public class UploadResponse {

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
}

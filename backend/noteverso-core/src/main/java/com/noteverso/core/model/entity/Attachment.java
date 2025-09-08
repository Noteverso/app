package com.noteverso.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@TableName(value = "noteverso_attachment", autoResultMap = true)
@Data
@Builder
public class Attachment {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 附件资源类型, image - 图片，file - 文件
     */
    private String resourceType;

    /**
     * 附件大小，单位 bytes
     */
    private Long size;

    /**
     * 附件id
     */
    private String attachmentId;

    private String creator;

    private String updater;

    private Instant addedAt;

    private Instant updatedAt;
}

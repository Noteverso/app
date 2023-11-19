package com.noteverso.core.model;

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
     * 附件资源类型，0 - image - 图片，1 - file - 文件
     */
    private Integer resourceType;

    /**
     * 附件大小
     */
    private Long size;

    /**
     * 笔记id，如果附件属于项目或评论，则为 null
     */
    private String noteId;

    /**
     * 项目id，如果附件属于笔记或评论，则它为 null
     */
    private String projectId;

    /**
     * 评论id，如果附件属于笔记或项目，则它为 null
     */
    private String commentId;

    private String creator;

    private String updater;

    private Instant addedAt;

    private Instant updatedAt;
}

package com.noteverso.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@TableName(value = "noteverso_comment", autoResultMap = true)
@Data
@Builder
public class Comment {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String commentId;
    private String content;
    /**
     * 笔记id，如果评论属于项目，则为 null
     */
    private String noteId;
    /**
     * 项目id，如果评论属于笔记，则它为 null
     */
    private String projectId;
    private String creator;
    private String updater;
    private Instant addedAt;
    private Instant updatedAt;
}

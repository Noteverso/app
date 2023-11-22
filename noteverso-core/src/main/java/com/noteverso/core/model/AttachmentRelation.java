package com.noteverso.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;


@Data
@TableName(value = "noteverso_attachment_map")
@Builder
public class AttachmentRelation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String attachmentId;
    private String noteId;
    private String projectId;
    private String commentId;
    private Integer isDeleted;
    private String creator;
    private String updater;
    private Instant addedAt;
    private Instant updatedAt;
}

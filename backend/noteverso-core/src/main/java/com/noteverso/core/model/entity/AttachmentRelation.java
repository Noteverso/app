package com.noteverso.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@TableName(value = "noteverso_attachment_map")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentRelation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String attachmentId;
    private String objectId;
    private Integer isDeleted;
    private String creator;
    private String updater;
    private Instant addedAt;
    private Instant updatedAt;
}

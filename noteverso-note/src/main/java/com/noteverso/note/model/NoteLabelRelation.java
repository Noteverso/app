package com.noteverso.note.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Note-Label Relation
 * @author byodian
 * @since 2023-11-07
 */

@TableName(value = "noteverso_note_label_map")
@Data
@Builder
public class NoteLabelRelation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String noteId;
    private String labelId;
    private Integer isDeleted;
    private Instant addedAt;
    private Instant updatedAt;
    private String creator;
    private String updater;
}

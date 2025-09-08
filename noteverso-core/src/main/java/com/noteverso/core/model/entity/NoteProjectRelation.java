package com.noteverso.core.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Note-Project Relation
 * @author byodian
 * @since 2023-11-07
 */

@TableName(value = "noteverso_note_project_map")
@Data
@Builder
public class NoteProjectRelation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String noteId;

    private String projectId;

    private Instant addedAt;

    private Instant updatedAt;

    private String creator;

    private String updater;
}

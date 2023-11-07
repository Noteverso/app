package com.noteverso.note.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Note Relation
 * @author noteverso
 * @since 2023-11-07
 */

@TableName(value = "noteverso_note_map", autoResultMap = true)
@Data
@Builder
public class NoteRelation {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String noteId;
    /**
     * 关联至此的笔记id
     */
    private String linkedNoteId;
    /**
     * 关联笔记UI布局方式
     * 0 list - 列表，1 board - 看板
     */
    private Integer viewStyle;
    private Instant addedAt;
    private Instant updatedAt;
    private Long creator;
    private Long updater;
}

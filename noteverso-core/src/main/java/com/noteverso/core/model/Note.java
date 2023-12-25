package com.noteverso.core.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Note Entity
 * @author byodian
 * @since 2023-11-07
 */

@TableName(value = "noteverso_note", autoResultMap = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String noteId;
    /**
     * 笔记类型
     * 0-普通笔记
     * 1-账户密码
     * 2-待办清单
     * 3-图表
     * 4-日程
     * 5-工具清单
     * 6-记账(订阅信息，可自动更新续费信息)
     */
    private Integer noteType;
    private String content;
    private Integer isPinned;
    private Integer isDeleted;
    private Integer isArchived;
    private Integer isFavorite;
    private String projectId;
    /**
     * 笔记状态 0 -  待处理，1 - 正在进行，2 - 已完成
     */
    private Integer status;
    private String creator;
    private String updater;
    private String url;
    private Instant addedAt;
    private Instant updatedAt;
}

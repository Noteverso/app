package com.noteverso.project.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@TableName(value = "noteverso_project", autoResultMap = true)
@Data
@Builder
public class Project {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String projectId;
    private String name;
    private String color;
    private Integer isFavorite;
    private Integer isArchived;
    private Integer isShared;
    private Integer childOrder;
    private String parentId;
    /**
     * 客户端笔记展示布局方式
     * 0 list - 列表, 1 board - 看板
     * */
    private Integer viewStyle;
    private String url;
    private Integer collapsed;
    private Instant addedAt;
    private Instant updatedAt;
    private Long creator;
    private Long updater;

}

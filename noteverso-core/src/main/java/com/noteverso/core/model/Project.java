package com.noteverso.core.model;

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
     * 是否为收件箱项目，0 - 否 1 - 是
     * 每个用户只能有一个收件箱项目
     */
    private Integer isInboxProject;
    private String url;
    private Integer isCollapsed;
    private Instant addedAt;
    private Instant updatedAt;
    private String creator;
    private String updater;

}

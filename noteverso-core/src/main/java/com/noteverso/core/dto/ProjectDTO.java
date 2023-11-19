package com.noteverso.core.dto;

import lombok.Data;


@Data
public class ProjectDTO {
    private Long id;

    private String projectId;
    private String name;
    private String color;
    private Integer isFavorite;
    private Integer isArchived;
    private Integer isShared;
    private Integer childOrder;
    private String parentId;
    private Integer viewStyle;
    private Integer isInboxProject;
    private String url;
    private Integer isCollapsed;
    private String tenantId;
}

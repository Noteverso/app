package com.noteverso.core.dto;

import lombok.Data;

@Data
public class ProjectItem {
    private String name;

    private String projectId;

    private Long noteCount;

    private Integer isFavorite;

    private String color;

    private boolean inboxProject;
}

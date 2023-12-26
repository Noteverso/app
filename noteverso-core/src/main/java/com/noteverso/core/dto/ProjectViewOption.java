package com.noteverso.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectViewOption {
    private String projectId;

    private Integer showArchived;

    private Integer showDeleted;
}

package com.noteverso.core.model.dto;

import lombok.Data;

@Data
public class NoteCountForProject {
    private String projectId;

    private Long noteCount;
}

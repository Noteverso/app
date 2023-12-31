package com.noteverso.core.dto;

import lombok.Data;

@Data
public class NoteCountForProject {
    private String projectId;

    private Long noteCount;
}

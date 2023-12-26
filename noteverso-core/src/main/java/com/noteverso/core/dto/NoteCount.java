package com.noteverso.core.dto;

import lombok.Data;

@Data
public class NoteCount {
    private String projectId;

    private Long noteCount;
}

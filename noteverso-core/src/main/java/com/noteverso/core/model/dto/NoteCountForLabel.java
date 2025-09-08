package com.noteverso.core.model.dto;

import lombok.Data;

@Data
public class NoteCountForLabel {
    private String labelId;

    private Long noteCount;
}

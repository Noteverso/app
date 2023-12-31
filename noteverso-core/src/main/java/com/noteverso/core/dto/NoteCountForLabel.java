package com.noteverso.core.dto;

import lombok.Data;

@Data
public class NoteCountForLabel {
    private String labelId;

    private Long noteCount;
}

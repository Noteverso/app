package com.noteverso.core.dto;

import lombok.Data;

@Data
public class ReferencedNoteCount {
    private String referencedNoteId;

    private Long referencedNoteCount;
}

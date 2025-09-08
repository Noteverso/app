package com.noteverso.core.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class NoteItem {
    private String content;
    private String noteId;
    private Integer isPinned;
    private Integer isArchived;
    private Integer isDeleted;
    private String addedAt;
    private String updatedAt;
    private Long commentCount;
    private Long referencingCount;
    private Long referencedCount;
    private Long attachmentCount;
    private String creator;
    private List<LabelItem> labels;
    private ProjectItem project;
}

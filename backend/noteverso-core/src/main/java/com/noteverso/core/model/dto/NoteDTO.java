package com.noteverso.core.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class NoteDTO {
    private String projectId;

    private String projectName;

    private String content;

    private String noteId;

    private Integer isPinned;

    private Integer isArchived;

    private Integer isDeleted;

    private String addedAt;

    private String updatedAt;

    @Schema(description = "Label ids")
    private List<String> labels;

    @Schema(description = "Referencing note ids")
    private List<String> referencingNotes;

    @Schema(description = "Referenced by note ids")
    private List<String> referencedNotes;

    @Schema(description = "Attachment ids")
    private List<String> attachments;
}

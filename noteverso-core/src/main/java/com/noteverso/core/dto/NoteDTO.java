package com.noteverso.core.dto;

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

    private Integer isFavorite;

    private Integer isDeleted;

    @Schema(description = "Label ids")
    private List<String> labels;

    @Schema(description = "Reference note ids")
    private List<String> references;

    @Schema(description = "Linked note ids")
    private List<String> linkedNotes;

    @Schema(description = "Attachment ids")
    private List<String> attachments;
}

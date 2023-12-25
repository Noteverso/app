package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class NoteCreateRequest {
    @NotBlank(message = "content is required")
    @Schema(description = "Content of Note", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "Label ids of Note")
    private List<String> labels;

    @Schema(description = "List of notes linked to")
    private List<String> linkedNotes;

    @Schema(description = "Attachment ids of Note, includes images and files")
    private List<String> files;

    @NotBlank(message = "projectId is required")
    @Schema(description = "Project of Note", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectId;
}

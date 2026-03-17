package com.noteverso.core.model.request;

import com.noteverso.core.validation.ContentRequired;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@ContentRequired
public class NoteCreateRequest {
    @Schema(description = "Content of Note (ProseMirror JSON format)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Object contentJson;

    @Schema(description = "Label ids of Note")
    private List<String> labels;

    @Schema(description = "List of notes linked to")
    private List<String> linkedNotes;

    @Schema(description = "Attachment ids of Note, includes images and files")
    private List<String> files;

    @Schema(description = "Project of Note", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectId;
}

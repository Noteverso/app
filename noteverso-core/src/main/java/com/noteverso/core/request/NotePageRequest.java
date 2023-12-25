package com.noteverso.core.request;

import com.noteverso.core.pagination.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotePageRequest extends PageRequest {
    @NotBlank(message = "project id is required")
    @Schema(description = "project id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String projectId;
}

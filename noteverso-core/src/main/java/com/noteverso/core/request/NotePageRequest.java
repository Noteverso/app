package com.noteverso.core.request;

import com.noteverso.core.pagination.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotePageRequest extends PageRequest {
    @Schema(description = "object id, project id or label id")
    private String objectId;

    @Schema(description = "view type (0: project, 1: upcoming, 2: today, 3: past, 4: attachment, 5: label)", hidden = true)
    private Integer viewType;

    private Integer isDeleted = 0;

    private Integer isArchived = 0;

    private boolean showPinned = false;

}

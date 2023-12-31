package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ViewOptionCreate {
    @Schema(description = "object id is required for project view, but not for TODAY, PAST, ATTACHMENT and UPCOMING")
    private String objectId;

    @Schema(description = "view type 0 - PROJECT, 1 - UPCOMING, 2 - TODAY, 3 - PAST, 4 - ATTACHMENT 5 - LABEL", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer viewType;

    @Schema(description = "view layout 0 - list, 1 - board", defaultValue = "0")
    private Integer viewMode;
}

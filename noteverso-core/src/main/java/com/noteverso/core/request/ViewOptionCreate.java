package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ViewOptionCreate {
    @Schema(description = "object id is required for project view, but not for TODAY, PAST, ATTACHMENT and UPCOMING")
    private String objectId;

    @Schema(description = "view type 0 - PROJECT, 1 - UPCOMING, 2 - TODAY, 3 - PAST, 4 - ATTACHMENT", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer viewType;

    @Schema(description = "view layout 0 - list, 1 - board", defaultValue = "0")
    private Integer viewMode;

    @Schema(description = "group by 0 - ADDED_AT, 1 - NOTE STATUS, 2 - NOTE_LABEL", defaultValue = "0")
    private Integer groupedBy;

    @Schema(description = "ordered by 0 - ADDED_AT, 1 - COMMENT_COUNT, 2 - LINKED_NOTE_COUNT", defaultValue = "0")
    private Integer orderedBy;

    @Schema(description = "order value 0 - ASC, 1 - DESC", defaultValue = "0")
    private Integer orderValue;

    @Schema(description = "is show archived notes, 0 - no, 1 - yes", defaultValue = "0")
    private Integer showArchivedNotes;
}

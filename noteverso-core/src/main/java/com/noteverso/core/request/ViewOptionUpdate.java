package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ViewOptionUpdate {
    @Schema(description = "view option id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String viewOptionId;

    @Schema(description = "view type 0 - PROJECT, 1 - UPCOMING, 2 - TODAY, 3 - PAST, 4 - ATTACHMENT", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer viewType;

    @Schema(description = "view layout 0 - list, 1 - board", defaultValue = "0")
    private Integer viewMode;

    @Schema(description = "show archived")
    private Integer showArchived;

    @Schema(description = "show deleted")
    private Integer showDeleted;

    @Schema(description = "show pinned")
    private Integer showPinned;

    @Schema(description = "show label list")
    private Integer showLabelList;

    @Schema(description = "show attachment count")
    private Integer showAttachmentCount;

    @Schema(description = "show comment count")
    private Integer showCommentCount;

    @Schema(description = "show relation count between notes")
    private Integer showRelationNoteCount;

    @Schema(description = "order by")
    private Integer orderedBy;

    @Schema(description = "order value")
    private Integer orderValue;

    @Schema(description = "group by 0 - REMOVE GROUPED, 1 - ADDED_AT, 2 - UPDATED_AT, 3 - NOTE_STATUS, 4 - NOTE_LABEL")
    private Integer groupedBy;
}

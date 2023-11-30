package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProjectCreateRequest {
    @Schema(description = "Name of project", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Color of project", example = "red", requiredMode = Schema.RequiredMode.REQUIRED)
    private String color;

    @Schema(description = "Order of project, beginning with 1", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer childOrder;

    @Schema(description = "Parent id of project", defaultValue = "null")
    private String parentId;

    @Schema(description = "Favorite of project 0 - no, 1 - yes", defaultValue = "0")
    private Integer isFavorite;

    @Schema(description = "Notes view mode of project，0 - list, 1 - board", defaultValue = "0")
    private Integer viewMode;
}

package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProjectCreateRequest {
    @Schema(description = "Name of Project", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Color of Project", example = "red", requiredMode = Schema.RequiredMode.REQUIRED)
    private String color;

    @Schema(description = "order of Project, beginning with 1", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer childOrder;

    @Schema(description = "parent id of Project", defaultValue = "null")
    private String parentId;

    @Schema(description = "favorite of Project 0 - no, 1 - yes", defaultValue = "0")
    private Integer isFavorite;

    @Schema(description = "the notes view style of Project，0 - list, 1 - board", defaultValue = "0")
    private Integer viewStyle;
}

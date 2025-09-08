package com.noteverso.core.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProjectCreateRequest {

    @Schema(description = "Name of project", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "project name cannot be blank")
    private String name;

    @Schema(description = "Color of project", example = "red", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "project color cannot be blank")
    private String color;

    // TODO
    @Schema(description = "Order of project, beginning with 1", example = "1")
    private Integer childOrder;

    // TODO
    @Schema(description = "Parent id of project", defaultValue = "null")
    private String parentId;

    @Schema(description = "Favorite of project 0 - no, 1 - yes", defaultValue = "0")
    private Integer isFavorite;

    // TODO
    @Schema(description = "Notes view mode of project，0 - list, 1 - board", defaultValue = "0")
    private Integer viewMode;
}

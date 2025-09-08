package com.noteverso.core.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjectUpdateRequest {
    @Schema(description = "Name of project", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "project name cannot be blank")
    private String name;

    @Schema(description = "Color of project", example = "red", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "project color cannot be blank")
    private String color;

    @Schema(description = "Favorite of project 0 - no, 1 - yes", defaultValue = "0")
    private Integer isFavorite;
}

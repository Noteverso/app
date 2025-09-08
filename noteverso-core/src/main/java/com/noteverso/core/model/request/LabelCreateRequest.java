package com.noteverso.core.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LabelCreateRequest {
    @Schema(description = "label name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "label name cannot be blank")
    private String name;

    @Schema(description = "label color", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "label color cannot be blank")
    private String color;

    @Schema(description = "is favorite 0 - no, 1 - yes", defaultValue = "0")
    private Integer isFavorite;
}

package com.noteverso.core.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LabelUpdateRequest {
    @Schema(description = "Label name", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Label name is required")
    private String name;

    @Schema(description = "Label color", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Label color is required")
    private String color;

    @Schema(description = "Is favorite")
    private Integer isFavorite;
}

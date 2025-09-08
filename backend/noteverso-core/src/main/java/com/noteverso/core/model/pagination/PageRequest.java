package com.noteverso.core.model.pagination;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "page size is required")
    @Schema(description = "page size", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageSize = 10L;

    @NotNull(message = "page index is required")
    @Schema(description = "page index", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long pageIndex = 1L;
}

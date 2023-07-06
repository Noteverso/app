package com.noteverso.note.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class NoteCreateRequest {
    @Schema(description = "Name of Note", defaultValue = "example")
    private String name;

    @NotNull(message = "addedAt is required")
    @Schema(description = "Date of Note")
    private Date addedAt;
}

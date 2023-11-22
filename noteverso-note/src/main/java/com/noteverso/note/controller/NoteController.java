package com.noteverso.note.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.common.api.ApiResult;
import com.noteverso.common.context.TenantContext;
import com.noteverso.note.model.Note;
import com.noteverso.note.request.NoteCreateRequest;
import com.noteverso.note.request.NoteUpdateRequest;
import com.noteverso.note.service.NoteService;
import com.noteverso.core.dao.UserMapper;
import com.noteverso.core.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.Optional;

@Tag(name = "Note", description = "Note management APIs")
@RestController
@RequestMapping("/api/v1/notes")
@AllArgsConstructor
@Slf4j
public class NoteController {
    private final NoteService noteService;

    @Operation(summary = "Create a Note", description = "Create a Note", tags = { "Post" })
    @PostMapping("")
    public ApiResult<Void> createNote(@Valid @RequestBody NoteCreateRequest request) {
        noteService.createNote(request);
        return ApiResult.success(null);
    }

    @Operation(summary = "Update a Note", description = "Update a Note", tags = { "PATCH" })
    @PatchMapping("/{id}")
    public ApiResult<Void> updateNote(@PathVariable("id") String id, @Valid @RequestBody NoteUpdateRequest request) {
        noteService.updateNote(id, request);
        return ApiResult.success(null);
    }

    @Operation(summary = "Retrieve a Note by Id", description = "Delete a note", tags = { "DELETE" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Note.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })})
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteNote(@PathVariable("id") String id) {
        noteService.deleteNote(id);
        return ApiResult.success(null);
    }
}

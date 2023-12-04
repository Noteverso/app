package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.model.Note;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NoteUpdateRequest;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import static com.noteverso.common.constant.NumConstants.NOTE_DATACENTER_ID;
import static com.noteverso.common.constant.NumConstants.NUM_31;

@Tag(name = "Note", description = "Note management APIs")
@RestController
@RequestMapping("/api/v1/notes")
@AllArgsConstructor
@Slf4j
public class NoteController {
    private final NoteService noteService;

    @Operation(summary = "Create a Note", description = "Create a Note", tags = { "Post" })
    @PostMapping("")
    public ApiResult<Void> createNote(Authentication authentication, @Valid @RequestBody NoteCreateRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        noteService.createNote(request, principal.getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Update a Note", description = "Update a Note", tags = { "PATCH" })
    @PatchMapping("/{id}")
    public ApiResult<Void> updateNote(Authentication authentication, @PathVariable("id") String id, @Valid @RequestBody NoteUpdateRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        noteService.updateNote(id, principal.getUserId(), request);
        return ApiResult.success(null);
    }

    @Operation(summary = "Delete a Note", description = "Delete a note", tags = { "DELETE" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Note.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })})
    @DeleteMapping("/{id}")
    public ApiResult<Void> deleteNote(@PathVariable("id") String id) {
        noteService.toggleVisibility(id, false);
        return ApiResult.success(null);
    }

    @Operation(summary = "Restore a Note", description = "Restore a note", tags = { "PATCH" })
    @PatchMapping("/{id}/restore")
    public ApiResult<Void> restoreNote(@PathVariable("id") String id) {
        noteService.toggleVisibility(id, true);
        return ApiResult.success(null);
    }


    @Operation(summary = "Archive/Unarchive a Note", description = "Archive/Unarchive a note", tags = { "PATCH" })
    @PatchMapping("/{id}/archive")
    public ApiResult<Void> updateArchiveStatus(@PathVariable("id") String id, @RequestParam Boolean toggle) {
        noteService.toggleArchive(id, toggle);
        return ApiResult.success(null);
    }

    @Operation(summary = "Favorite/RemoveFavorite a Note", description = "Favorite/RemoveFavorite a note", tags = { "PATCH" })
    @PatchMapping("/{id}/favorite")
    public ApiResult<Void> updateFavoriteStatus(@PathVariable("id") String id, @RequestParam Boolean toggle) {
        noteService.toggleFavorite(id, toggle);
        return ApiResult.success(null);
    }

    @Operation(summary = "Pin/Unpin a Note", description = "Pin/Unpin a note", tags = { "PATCH" })
    @PatchMapping("/{id}/pin")
    public ApiResult<Void> updatePinStatus(@PathVariable("id") String id, @RequestParam Boolean toggle) {
        noteService.togglePin(id, toggle);
        return ApiResult.success(null);
    }

    @Operation(summary = "Move a note", description = "Move a note", tags = { "PATCH" })
    @PatchMapping("/{id}/move")
    public ApiResult<Void> moveNote(@PathVariable("id") String id, @RequestParam String projectId) {
        noteService.moveNote(id, projectId);
        return ApiResult.success(null);
    }
}

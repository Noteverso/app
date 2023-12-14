package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.manager.impl.AuthManagerImpl;
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

@Tag(name = "Note", description = "Note management APIs")
@RestController
@RequestMapping("/api/v1/notes")
@AllArgsConstructor
@Slf4j
public class NoteController {
    private final NoteService noteService;
    private static final AuthManager authManager = new AuthManagerImpl();

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

    @Operation(summary = "Move a Note to Trash", description = "Move a Note to Trash", tags = { "DELETE" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Note.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })})
    @DeleteMapping("/{id}")
    public ApiResult<Void> moveNoteToTrash(@PathVariable("id") String id) {
        noteService.moveNoteToTrash(id);
        return ApiResult.success(null);
    }

    @Operation(summary = "Restore a Note", description = "Restore a note", tags = { "PATCH" })
    @PatchMapping("/{id}/restore")
    public ApiResult<Void> restoreNote(Authentication authentication, @PathVariable("id") String id) {
        noteService.restoreNote(id, authManager.getPrincipal(authentication).getUserId());
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
    public ApiResult<Void> moveNote(Authentication authentication, @PathVariable("id") String id, @RequestParam String projectId) {
        noteService.moveNote(id, projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Delete a note permanently", description = "Delete a note permanently", tags = { "DELETE" })
    @DeleteMapping("/{id}/permanent")
    public ApiResult<Void> deleteNotePermanent(Authentication authentication, @PathVariable("id") String id) {
        noteService.deleteNote(id, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }
}

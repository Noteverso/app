package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.core.dto.NoteDTO;
import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.manager.impl.AuthManagerImpl;
import com.noteverso.core.model.Note;
import com.noteverso.core.pagination.PageResult;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NotePageRequest;
import com.noteverso.core.request.NoteUpdateRequest;
import com.noteverso.core.dto.NoteItem;
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
import java.util.List;

@Tag(name = "Note", description = "Note management APIs")
@RestController
@RequestMapping("/api/v1/notes")
@AllArgsConstructor
@Slf4j
public class NoteController {
    private final NoteService noteService;
    private static final AuthManager authManager = new AuthManagerImpl();

    @Operation(summary = "Create a Note", description = "Create a Note", tags = { "POST" })
    @PostMapping("")
    public ApiResult<String> createNote(Authentication authentication, @Valid @RequestBody NoteCreateRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        String noteId = noteService.createNote(request, principal.getUserId());
        return ApiResult.success(noteId);
    }

    @Operation(summary = "Get Notes Page by Project", description = "Get Notes Page by Project", tags = { "GET" })
    @GetMapping("/project")
    public ApiResult<PageResult<NoteItem>> getNotesByProject(Authentication authentication, @Valid NotePageRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        PageResult<NoteItem> notePageResponsePage = noteService.getNotePageByProject(request, principal.getUserId());
        return ApiResult.success(notePageResponsePage);
    }

    @Operation(summary = "Get Notes Page by Label", description = "Get Notes Page by Label", tags = { "GET" })
    @GetMapping("/label")
    public ApiResult<PageResult<NoteItem>> getNotesByLabel(Authentication authentication, @Valid NotePageRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        PageResult<NoteItem> notePageResponsePage = noteService.getNotePageByLabel(request, principal.getUserId());
        return ApiResult.success(notePageResponsePage);
    }

    @Operation(summary = "Get a Note", description = "Get a Note", tags = { "GET" })
    @GetMapping("/{id}")
    public ApiResult<NoteDTO> getNote(Authentication authentication, @PathVariable("id") String id) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        NoteDTO note = noteService.getNoteDetail(id, principal.getUserId());
        return ApiResult.success(note);
    }

    @Operation(summary = "Update a Note", description = "Update a Note", tags = { "PATCH" })
    @PatchMapping("/{id}")
    public ApiResult<Void> updateNote(Authentication authentication, @PathVariable("id") String id, @Valid @RequestBody NoteUpdateRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        noteService.updateNote(id, principal.getUserId(), request);
        return ApiResult.success(null);
    }

    @Operation(summary = "Get referenced notes", description = "Get referenced notes", tags = { "GET" })
    @GetMapping("/{id}/referenced")
    public ApiResult<List<NoteItem>> getReferencedNotes(Authentication authentication, @PathVariable("id") String id) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        List<NoteItem> notes = noteService.getReferencedNotes(id, principal.getUserId());
        return ApiResult.success(notes);
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

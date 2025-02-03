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
    public String createNote(Authentication authentication, @Valid @RequestBody NoteCreateRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return noteService.createNote(request, principal.getUserId());
    }

    @Operation(summary = "Get Notes Page by Project", description = "Get Notes Page by Project", tags = { "GET" })
    @GetMapping("/project")
    public PageResult<NoteItem> getNotesByProject(Authentication authentication, @Valid NotePageRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return noteService.getNotePageByProject(request, principal.getUserId());
    }

    @Operation(summary = "Get Notes Page by Label", description = "Get Notes Page by Label", tags = { "GET" })
    @GetMapping("/label")
    public PageResult<NoteItem> getNotesByLabel(Authentication authentication, @Valid NotePageRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return noteService.getNotePageByLabel(request, principal.getUserId());
    }

    @Operation(summary = "Get a Note", description = "Get a Note", tags = { "GET" })
    @GetMapping("/{id}")
    public NoteDTO getNote(Authentication authentication, @PathVariable("id") String id) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return noteService.getNoteDetail(id, principal.getUserId());
    }

    @Operation(summary = "Update a Note", description = "Update a Note", tags = { "PATCH" })
    @PatchMapping("/{id}")
    public Void updateNote(Authentication authentication, @PathVariable("id") String id, @Valid @RequestBody NoteUpdateRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        noteService.updateNote(id, principal.getUserId(), request);
        return null;
    }

    @Operation(summary = "Get referenced notes", description = "Get referenced notes", tags = { "GET" })
    @GetMapping("/{id}/referenced")
    public List<NoteItem> getReferencedNotes(Authentication authentication, @PathVariable("id") String id) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return noteService.getReferencedNotes(id, principal.getUserId());
    }

    @Operation(summary = "Move a Note to Trash", description = "Move a Note to Trash", tags = { "DELETE" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Note.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })})
    @DeleteMapping("/{id}")
    public Void moveNoteToTrash(@PathVariable("id") String id) {
        noteService.moveNoteToTrash(id);
        return null;
    }

    @Operation(summary = "Restore a Note", description = "Restore a note", tags = { "PATCH" })
    @PatchMapping("/{id}/restore")
    public Void restoreNote(Authentication authentication, @PathVariable("id") String id) {
        noteService.restoreNote(id, authManager.getPrincipal(authentication).getUserId());
        return null;
    }


    @Operation(summary = "Archive/Unarchive a Note", description = "Archive/Unarchive a note", tags = { "PATCH" })
    @PatchMapping("/{id}/archive")
    public Void updateArchiveStatus(@PathVariable("id") String id, @RequestParam Boolean toggle) {
        noteService.toggleArchive(id, toggle);
        return null;
    }

    @Operation(summary = "Favorite/RemoveFavorite a Note", description = "Favorite/RemoveFavorite a note", tags = { "PATCH" })
    @PatchMapping("/{id}/favorite")
    public Void updateFavoriteStatus(@PathVariable("id") String id, @RequestParam Boolean toggle) {
        noteService.toggleFavorite(id, toggle);
        return null;
    }

    @Operation(summary = "Pin/Unpin a Note", description = "Pin/Unpin a note", tags = { "PATCH" })
    @PatchMapping("/{id}/pin")
    public Void updatePinStatus(@PathVariable("id") String id, @RequestParam Boolean toggle) {
        noteService.togglePin(id, toggle);
        return null;
    }

    @Operation(summary = "Move a note", description = "Move a note", tags = { "PATCH" })
    @PatchMapping("/{id}/move")
    public Void moveNote(Authentication authentication, @PathVariable("id") String id, @RequestParam String projectId) {
        noteService.moveNote(id, projectId, authManager.getPrincipal(authentication).getUserId());
        return null;
    }

    @Operation(summary = "Delete a note permanently", description = "Delete a note permanently", tags = { "DELETE" })
    @DeleteMapping("/{id}/permanent")
    public Void deleteNotePermanent(Authentication authentication, @PathVariable("id") String id) {
        noteService.deleteNote(id, authManager.getPrincipal(authentication).getUserId());
        return null;
    }
}

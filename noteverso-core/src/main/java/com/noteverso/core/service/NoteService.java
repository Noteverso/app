package com.noteverso.core.service;

import com.noteverso.core.dto.NoteDTO;
import com.noteverso.core.pagination.PageResult;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NotePageRequest;
import com.noteverso.core.request.NoteUpdateRequest;
import com.noteverso.core.dto.NoteItem;

import java.util.List;

public interface NoteService {
    String createNote(NoteCreateRequest request, String userId);

    void updateNote(String id, String userId, NoteUpdateRequest request);

    /**
     * move a note to trash
     * @param id
     */
    void moveNoteToTrash(String id);

    /**
     * Restore a note from trash
     * @param id
     */
    void restoreNote(String id, String userId);

    /**
     * Archive/Unarchive a note
     * @param id
     * @param toggle true - archive false - unarchive
     */
    void toggleArchive(String id, Boolean toggle);

    /**
     * Favorite/RemoveFavorite a note
     * @param id
     * @param toggle true - favorite false - remove
     */
    void toggleFavorite(String id, Boolean toggle);

    /**
     * Pin/Unpin a note
     * @param id
     * @param toggle true - pin false - unpin
     */
    void togglePin(String id, Boolean toggle);

    void moveNote(String id, String projectId, String userId);

    void deleteNote(String id, String userId);

    NoteDTO getNoteDetail(String noteId, String userId);

    /**
     * modify the note view option and return the page of notes
     * @param request
     * @param userId
     * @return
     */
    PageResult<NoteItem> getNotePageByProject(NotePageRequest request, String userId);

    PageResult<NoteItem> getNotePageByLabel(NotePageRequest request, String userId);

    List<NoteItem> getReferencedNotes(String noteId, String userId);
}

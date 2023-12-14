package com.noteverso.core.service;

import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NoteUpdateRequest;

public interface NoteService {
    void createNote(NoteCreateRequest request, String userId);

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
}

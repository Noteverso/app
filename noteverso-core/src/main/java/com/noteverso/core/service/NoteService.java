package com.noteverso.core.service;

import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NoteUpdateRequest;

public interface NoteService {
    void createNote(NoteCreateRequest request, String tenantId);

    void updateNote(String id, String tenantId, NoteUpdateRequest request);

    /**
     * Delete/Restore a note
     * @param id
     * @param toggle true - restore false - delete
     */
    void toggleVisibility(String id, Boolean toggle);

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

    void moveNote(String id, String projectId);
}

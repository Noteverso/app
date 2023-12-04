package com.noteverso.core.service;

import com.noteverso.core.model.Note;
import com.noteverso.core.request.NoteCreateRequest;

import java.util.List;

public abstract class NoteServiceTestFixture {
    protected Note createNote(String noteId, String content, String projectId) {
        return Note.builder().noteId(noteId).content(content).projectId(projectId).build();
    }

    protected NoteCreateRequest createMinimalNoteRequest(String content, String projectId) {
        return createNoteRequest(content, projectId, null, null, null);
    }

    protected NoteCreateRequest createNoteRequestWithLabels(String content, String projectId, List<String> labels) {
        return createNoteRequest(content, projectId, labels, null, null);
    }

    protected NoteCreateRequest createNoteRequestWithAttachments(String content, String projectId, List<String> attachments) {
        return createNoteRequest(content, projectId, null, attachments, null);
    }

    protected NoteCreateRequest createNoteRequestWithLinkedNotes(String content, String projectId, List<String> linkedNotes) {
        return createNoteRequest(content, projectId, null, null, linkedNotes);
    }

    protected NoteCreateRequest createNoteRequest(String content, String projectId, List<String> labels, List<String> attachments, List<String> linkedNotes) {
        NoteCreateRequest noteCreateRequest = new NoteCreateRequest();
        noteCreateRequest.setContent(content);
        noteCreateRequest.setProjectId(projectId);
        noteCreateRequest.setLinkedNotes(linkedNotes);
        noteCreateRequest.setFiles(attachments);
        noteCreateRequest.setLabels(labels);
        return noteCreateRequest;
    }
}

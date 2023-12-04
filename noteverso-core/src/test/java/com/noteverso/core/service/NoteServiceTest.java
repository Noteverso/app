package com.noteverso.core.service;

import com.noteverso.common.exceptions.BaseException;
import com.noteverso.core.dao.AttachmentRelationMapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.NoteRelationMapper;
import com.noteverso.core.model.Note;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;


import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class NoteServiceTest extends NoteServiceTestFixture {
    private static final String noteId = "1";
    private static final String content = "Hello World!";
    private static final String projectId = "1";

    private static final int TIME_1 = 1;
    private static final String tenantId = "test";
    @Mock
    NoteMapper noteMapper;

    @Mock
    NoteRelationMapper noteRelationMapper;

    @Mock
    NoteLabelRelationMapper noteLabelRelationMapper;

    @Mock
    AttachmentRelationMapper attachmentRelationMapper;

    @InjectMocks
    NoteServiceImpl noteService;

    @Test
    void should_createNoteSuccessfully_forMinimalNote() {
        // Arrange
        NoteCreateRequest minimalNoteCreateRequest = createMinimalNoteRequest(content, projectId);

        // Act
        noteService.createNote(minimalNoteCreateRequest, tenantId);

        // Assert
        verify(noteMapper, times(TIME_1)).insert(any(Note.class));
        int TIME_0 = 0;
        verify(noteLabelRelationMapper, times(TIME_0)).batchInsert(anyList());
        verify(noteRelationMapper, times(TIME_0)).batchInsert(anyList());
        verify(attachmentRelationMapper, times(TIME_0)).batchInsert(anyList());
    }

    @Test
    void should_createNoteSuccessfully_forNoteCreateRequestWithLabels() {
        // Arrange
        NoteCreateRequest noteCreateRequestWithLabels = createNoteRequestWithLabels(content, projectId, List.of("1", "2"));

        // Act
        noteService.createNote(noteCreateRequestWithLabels, tenantId);

        // Assert
        verify(noteLabelRelationMapper, times(TIME_1)).batchInsert(anyList());
    }

    @Test
    void should_createNoteSuccessfully_forNoteCreateRequestWithLinkedNotes() {
        // Arrange
        NoteCreateRequest noteCreateRequestWithLinkedNotes = createNoteRequestWithLinkedNotes(content, projectId, List.of("1", "2"));

        // Act
        noteService.createNote(noteCreateRequestWithLinkedNotes, tenantId);

        // Assert
        verify(noteRelationMapper, times(TIME_1)).batchInsert(anyList());
    }

    @Test
    void should_createNoteSuccessfully_forNoteCreateRequestWithAttachments() {
        // Arrange
        NoteCreateRequest noteCreateRequestWithAttachments = createNoteRequestWithAttachments(content, projectId, List.of("1", "2"));

        // Act
        noteService.createNote(noteCreateRequestWithAttachments, tenantId);

        // Assert
        verify(attachmentRelationMapper, times(TIME_1)).batchInsert(anyList());
    }



    @Test
    @DisplayName("Test should Pass When Comment do not contain Swear Words")
    public void shouldNotContainSwearWords() {
        NoteService noteService = new NoteServiceImpl(null, null, null, null);
        boolean containsSwearWords = noteService.containsSwearWords("test");
        assertFalse(containsSwearWords);
    }

    @Test
    @DisplayName("Should Throw Exception when Exception Contains Swear Words")
    public void shouldThrowExceptionWhenContainsSwearWords() {
        NoteService noteService = new NoteServiceImpl(null, null, null, null);
        BaseException exception = assertThrows(BaseException.class, () -> {
            noteService.containsSwearWords("This is shitty comment");
        });
        assertTrue(exception.getMessage().contains("Comments contains unacceptable language"));
        assertThatThrownBy(() -> {
            noteService.containsSwearWords("This is shitty comment");
        }).isInstanceOf(BaseException.class).hasMessage("Comments contains unacceptable language");
    }
}

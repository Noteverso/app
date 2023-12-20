package com.noteverso.core.service;

import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dto.NoteDTO;
import com.noteverso.core.model.Note;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
    @Mock
    NoteMapper noteMapper;

    @Mock
    SnowFlakeUtils snowFlakeUtils;

    @Mock
    RelationService relationService;

    @Mock
    ProjectMapper projectMapper;

    @InjectMocks
    NoteServiceImpl noteService;

    @Test
    void should_createNoteSuccessfully_withMinimalNote() {
        // Arrange
        NoteCreateRequest noteCreateRequest = new NoteCreateRequest();
        noteCreateRequest.setContent("Hello World!");
        noteCreateRequest.setProjectId("1");
        String tenantId = "test";

        // Act
        noteService.createNote(noteCreateRequest, tenantId);

        // Assert
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteMapper).insert(noteCaptor.capture());
        Note captureedNote = noteCaptor.getValue();
        assertThat(captureedNote.getContent()).isEqualTo(noteCreateRequest.getContent());
    }

    @Test
    void should_deleteNoteSuccessfully() {
        // Arrange
        String noteId = "1";
        String userId = "test";
        when(noteMapper.delete(any())).thenReturn(1);

        // Act
        noteService.deleteNote(noteId, userId);

        // Assert
        verify(relationService, times(1)).deleteNoteRelation(noteId, userId);
    }

    @Test
    void deleteNote_shouldThrowException_whenNoteIsNotFound() {
        // Arrange
        String noteId = "1";
        String userId = "test";
        when(noteMapper.delete(any())).thenReturn(0);

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> noteService.deleteNote(noteId, userId);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Note not found");
    }

    @Test
    void moveNote_shouldThrowException_whenProjectIsNotFound() {
        // Arrange
        String noteId = "1";
        String projectId = "1";
        String userId = "test";
        when(projectMapper.selectByProjectId(projectId, userId)).thenReturn(null);

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> noteService.moveNote(noteId, projectId, userId);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Project not found");
    }

    @Test
    void should_returnNoteDetail_whenGetNoteDetail() {
        // Arrange
        String noteId = "1";
        String userId = "test";

        // Act
        NoteDTO noteDTO = noteService.getNoteDetail(noteId, userId);

        // Assert
        // TODO
        // 1. NoteLabelRelation
        // 2. AttachmentRelation
        // 3. NoteRelation
    }
}

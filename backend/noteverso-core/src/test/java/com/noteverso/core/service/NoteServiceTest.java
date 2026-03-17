package com.noteverso.core.service;

import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.model.dto.NoteDTO;
import com.noteverso.core.model.entity.Note;
import com.noteverso.core.model.request.NoteCreateRequest;
import com.noteverso.core.service.impl.NoteServiceImpl;

import java.util.List;
import java.util.Map;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
        noteCreateRequest.setContentJson(Map.of("type", "doc", "content", List.of(Map.of("type", "paragraph", "content", List.of(Map.of("type", "text", "text", "Hello World!"))))));
        noteCreateRequest.setProjectId("1");
        String tenantId = "test";

        // Act
        noteService.createNote(noteCreateRequest, tenantId);

        // Assert
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteMapper).insertWithJsonb(noteCaptor.capture());
        Note captureedNote = noteCaptor.getValue();
        assertThat(captureedNote.getContentJson()).isEqualTo(noteCreateRequest.getContentJson());
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
        Note note = Note.builder()
                .contentJson(Map.of("type", "doc", "content", List.of(Map.of("type", "paragraph", "content", List.of(Map.of("type", "text", "text", "Hello World!")))))).creator(userId).updater(userId).noteId(noteId)
                .isPinned(0).isFavorite(0).isArchived(0).isDeleted(0)
                .build();

        when(relationService.getReferencingNotes(noteId, userId)).thenReturn(List.of("2", "3"));
        when(relationService.getReferencedNotes(noteId, userId)).thenReturn(List.of("4", "5"));
        when(relationService.getLabelsByNoteId(noteId, userId)).thenReturn(List.of("1", "2"));
        when(relationService.getAttachmentsByNoteId(noteId, userId)).thenReturn(List.of("1", "2"));
        when(noteMapper.selectByNoteId(noteId, 0)).thenReturn(note);

        // Act
        NoteDTO noteDTO = noteService.getNoteDetail(noteId, userId);

        // Assert
        assertThat(noteDTO.getReferencedNotes()).isEqualTo(List.of("4", "5"));
    }

    @Test
    void getNoteDetails_shouldThrowException_whenNoteIsNotFound() {
        // Arrange
        String noteId = "1";
        String userId = "test";
        when(noteMapper.selectByNoteId(noteId, 0)).thenReturn(null);

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> noteService.getNoteDetail(noteId, userId);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Note not found");
    }

    @Test
    void should_createNote_withEmptyContent() {
        // Arrange
        NoteCreateRequest request = new NoteCreateRequest();
        request.setContentJson(Map.of("type", "doc", "content", List.of()));
        request.setProjectId("1");
        String userId = "test";

        // Act
        noteService.createNote(request, userId);

        // Assert
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteMapper).insertWithJsonb(noteCaptor.capture());
        assertThat(noteCaptor.getValue().getContentJson()).isEqualTo(Map.of("type", "doc", "content", List.of()));
    }

    @Test
    void should_createNote_handleNullLabels() {
        // Arrange
        NoteCreateRequest request = new NoteCreateRequest();
        request.setContentJson(Map.of("type", "doc", "content", List.of(Map.of("type", "paragraph", "content", List.of(Map.of("type", "text", "text", "Test"))))));
        request.setProjectId("1");
        request.setLabels(null);
        String userId = "test";

        // Act
        noteService.createNote(request, userId);

        // Assert
        verify(noteMapper).insertWithJsonb(any(Note.class));
        verify(relationService).insertNoteLabelRelation(eq(null), any(), eq(userId));
    }

    @Test
    void should_createNote_handleEmptyLabels() {
        // Arrange
        NoteCreateRequest request = new NoteCreateRequest();
        request.setContentJson(Map.of("type", "doc", "content", List.of(Map.of("type", "paragraph", "content", List.of(Map.of("type", "text", "text", "Test"))))));
        request.setProjectId("1");
        request.setLabels(List.of());
        String userId = "test";

        // Act
        noteService.createNote(request, userId);

        // Assert
        verify(noteMapper).insertWithJsonb(any(Note.class));
        verify(relationService).insertNoteLabelRelation(eq(List.of()), any(), eq(userId));
    }
}

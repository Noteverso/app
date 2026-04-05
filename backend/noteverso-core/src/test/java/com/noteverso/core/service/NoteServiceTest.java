package com.noteverso.core.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.common.exceptions.RequestVerifyException;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.model.dto.NoteDTO;
import com.noteverso.core.model.entity.Note;
import com.noteverso.core.model.entity.Project;
import com.noteverso.core.model.request.NoteCreateRequest;
import com.noteverso.core.model.request.NoteUpdateRequest;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
    @BeforeAll
    static void initTableInfo() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Note.class);
    }

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

    @Test
    void should_updateNoteSuccessfully_whenNoteExistsAndProjectIsValid() {
        // Arrange
        String noteId = "note-1";
        String userId = "user-1";
        NoteUpdateRequest request = new NoteUpdateRequest();
        request.setContentJson(Map.of("type", "doc", "content", List.of()));
        request.setProjectId("project-2");
        request.setLabels(List.of("label-1"));
        request.setLinkedNotes(List.of("linked-1"));
        request.setFiles(List.of("file-1"));

        when(noteMapper.selectOne(any())).thenReturn(Note.builder().noteId(noteId).creator(userId).projectId("project-1").build());
        when(projectMapper.selectByProjectId("project-2", userId)).thenReturn(Project.builder().projectId("project-2").build());

        // Act
        noteService.updateNote(noteId, userId, request);

        // Assert
        verify(noteMapper).selectOne(any());
        verify(projectMapper).selectByProjectId("project-2", userId);
        verify(noteMapper).updateNoteWithJsonb(eq(noteId), eq(userId), eq(request.getContentJson()), eq("project-2"), any(Instant.class), eq(userId));
        verify(relationService).updateNoteLabelRelation(request.getLabels(), noteId, userId);
        verify(relationService).updateNoteRelation(request.getLinkedNotes(), noteId, userId);
        verify(relationService).updateNoteAttachment(request.getFiles(), noteId, userId);
    }

    @Test
    void should_updateNoteWithoutChangingProject_whenProjectIdIsNull() {
        // Arrange
        String noteId = "note-1";
        String userId = "user-1";
        NoteUpdateRequest request = new NoteUpdateRequest();
        request.setContentJson(Map.of("type", "doc", "content", List.of()));
        request.setLabels(List.of());
        request.setLinkedNotes(List.of());
        request.setFiles(List.of());

        when(noteMapper.selectOne(any())).thenReturn(Note.builder().noteId(noteId).creator(userId).projectId("project-1").build());

        // Act
        noteService.updateNote(noteId, userId, request);

        // Assert
        verify(projectMapper, never()).selectByProjectId(anyString(), anyString());
        verify(noteMapper).updateNoteWithJsonb(eq(noteId), eq(userId), eq(request.getContentJson()), isNull(), any(Instant.class), eq(userId));
        verify(relationService).updateNoteLabelRelation(request.getLabels(), noteId, userId);
        verify(relationService).updateNoteRelation(request.getLinkedNotes(), noteId, userId);
        verify(relationService).updateNoteAttachment(request.getFiles(), noteId, userId);
    }

    @Test
    void updateNote_shouldThrowException_whenNoteIsNotFound() {
        // Arrange
        String noteId = "note-1";
        String userId = "user-1";
        NoteUpdateRequest request = new NoteUpdateRequest();
        request.setContentJson(Map.of("type", "doc", "content", List.of()));

        when(noteMapper.selectOne(any())).thenReturn(null);

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> noteService.updateNote(noteId, userId, request);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Note not found");
        verify(noteMapper, never()).updateNoteWithJsonb(anyString(), anyString(), any(), any(), any(), anyString());
    }

    @Test
    void updateNote_shouldThrowException_whenProjectIdIsBlank() {
        // Arrange
        String noteId = "note-1";
        String userId = "user-1";
        NoteUpdateRequest request = new NoteUpdateRequest();
        request.setContentJson(Map.of("type", "doc", "content", List.of()));
        request.setProjectId("   ");

        when(noteMapper.selectOne(any())).thenReturn(Note.builder().noteId(noteId).creator(userId).build());

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> noteService.updateNote(noteId, userId, request);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(RequestVerifyException.class).hasMessage("Project id must not be blank");
        verify(projectMapper, never()).selectByProjectId(anyString(), anyString());
        verify(noteMapper, never()).updateNoteWithJsonb(anyString(), anyString(), any(), any(), any(), anyString());
    }

    @Test
    void updateNote_shouldThrowException_whenProjectIsNotFound() {
        // Arrange
        String noteId = "note-1";
        String userId = "user-1";
        NoteUpdateRequest request = new NoteUpdateRequest();
        request.setContentJson(Map.of("type", "doc", "content", List.of()));
        request.setProjectId("missing-project");

        when(noteMapper.selectOne(any())).thenReturn(Note.builder().noteId(noteId).creator(userId).build());
        when(projectMapper.selectByProjectId("missing-project", userId)).thenReturn(null);

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> noteService.updateNote(noteId, userId, request);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Project not found");
        verify(noteMapper, never()).updateNoteWithJsonb(anyString(), anyString(), any(), any(), any(), anyString());
    }
}

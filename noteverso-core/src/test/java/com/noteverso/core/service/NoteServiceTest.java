package com.noteverso.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dao.ViewOptionMapper;
import com.noteverso.core.dto.NoteDTO;
import com.noteverso.core.model.Note;
import com.noteverso.core.model.ViewOption;
import com.noteverso.core.pagination.PageResult;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NotePageRequest;
import com.noteverso.core.response.NotePageResponse;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

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

    @Mock
    ViewOptionMapper viewOptionMapper;

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
        Note note = Note.builder()
                .content("Hello World!").creator(userId).updater(userId).noteId(noteId)
                .isPinned(0).isFavorite(0).isArchived(0).isDeleted(0)
                .build();

        when(relationService.getReferencedNotesFromNote(noteId, userId)).thenReturn(List.of("2", "3"));
        when(relationService.getReferringNotesToNote(noteId, userId)).thenReturn(List.of("4", "5"));
        when(relationService.getLabelsByNoteId(noteId, userId)).thenReturn(List.of("1", "2"));
        when(relationService.getAttachmentsByNoteId(noteId, userId)).thenReturn(List.of("1", "2"));
        when(noteMapper.selectByNoteId(noteId, 0)).thenReturn(note);

        // Act
        NoteDTO noteDTO = noteService.getNoteDetail(noteId, userId);

        // Assert
        assertThat(noteDTO.getLinkedNotes()).isEqualTo(List.of("4", "5"));
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
    void should_returnNotesPage_whenGetNotesByProjectAndViewOptionIsNull() {
        // Arrange
        String userId = "1";
        NotePageRequest request = new NotePageRequest();
        request.setProjectId("123");
        request.setPageSize(10L);
        request.setPageIndex(1L);

        QueryWrapper<Note> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", "123");
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.eq("is_archived", 0);
        queryWrapper.orderByDesc("is_pinned", "added_at");

        Page<Note> notePage = new Page<>();
        List<String> noteIds = List.of("1", "2", "3", "4", "5");
        List<Note> notes = constructNotesByNoteIds(noteIds, userId);
        notePage.setRecords(notes);
        notePage.setCurrent(request.getPageIndex());
        notePage.setSize(request.getPageSize());
        notePage.setTotal(notes.size());

        when(viewOptionMapper.selectOne(any())).thenReturn(null);
        when(noteMapper.selectPage(any(), any())).thenReturn(notePage);

        // Act
        PageResult<NotePageResponse> notePageResponsePage = noteService.getNotePage(request, userId);

        // Assert
        verify(relationService, times(0)).getReferencedCountByReferencedNoteIds(noteIds, userId);
        assertThat(notePageResponsePage.getTotal()).isEqualTo(notePage.getTotal());
    }

    @Test
    void should_returnNotesPage_whenGetNotesByProjectAndViewOptionIsNotNull() {
        // Arrange
        String userId = "1";
        NotePageRequest request = new NotePageRequest();
        request.setProjectId("123");
        request.setPageSize(10L);
        request.setPageIndex(1L);

        Page<Note> notePage = new Page<>();
        List<String> noteIds = List.of("1", "2", "3", "4", "5");
        List<Note> notes = constructNotesByNoteIds(noteIds, userId);
        notePage.setRecords(notes);
        notePage.setCurrent(request.getPageIndex());
        notePage.setSize(request.getPageSize());
        notePage.setTotal(notes.size());
        when(noteMapper.selectPage(any(), any())).thenReturn(notePage);

        ViewOption viewOption = new ViewOption();
        viewOption.setShowRelationNoteCount(1);
        viewOption.setOrderedBy(0);
        when(viewOptionMapper.selectOne(any())).thenReturn(viewOption);

        // Act
        PageResult<NotePageResponse> notePageResponsePage = noteService.getNotePage(request, userId);

        // Assert
        verify(relationService, times(1)).getReferencedCountByReferencedNoteIds(noteIds, userId);
        assertThat(notePageResponsePage.getTotal()).isEqualTo(notePage.getTotal());
    }

    private List<Note> constructNotesByNoteIds(List<String> noteIds, String userId) {
        List<Note> notes = new ArrayList<>();
        for (String noteId : noteIds) {
            notes.add(Note.builder()
                    .content("Hello World" + noteId + "!")
                    .creator(userId)
                    .updater(userId)
                    .noteId(noteId)
                    .isPinned(0).isFavorite(0).isArchived(0).isDeleted(0)
                    .build());
        }
        return notes;
    }
}

package com.noteverso.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dto.NoteDTO;
import com.noteverso.core.dto.NoteItem;
import com.noteverso.core.model.Note;
import com.noteverso.core.model.NoteLabelRelation;
import com.noteverso.core.model.Project;
import com.noteverso.core.model.ViewOption;
import com.noteverso.core.pagination.PageResult;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NotePageRequest;
import com.noteverso.core.request.ProjectRequest;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Mock
    ViewOptionService viewOptionService;

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
    void should_returnNotesPageByProject_whenViewOptionIsNull() {
        // Arrange
        String userId = "1";
        String projectId = "123";
        NotePageRequest request = new NotePageRequest();
        request.setObjectId(projectId);
        request.setViewType(0);
        request.setPageSize(10L);
        request.setPageIndex(1L);

        Page<Note> notePage = new Page<>();
        List<String> noteIds = List.of("1", "2", "3", "4", "5");
        List<Note> notes = constructNotesByNoteIds(noteIds, userId, projectId);
        notePage.setRecords(notes);
        notePage.setCurrent(request.getPageIndex());
        notePage.setSize(request.getPageSize());
        notePage.setTotal(notes.size());

        List<NoteLabelRelation> noteLabelRelations = new ArrayList<>();

        when(viewOptionService.getViewOption(any(ViewOption.class), any(String.class))).thenReturn(null);
        when(noteMapper.selectPage(any(), any())).thenReturn(notePage);

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectIds(Collections.singleton(projectId));
        Project project = new Project();
        project.setProjectId(projectId);
        project.setName("projectTestName");
        project.setCreator(userId);
        when(projectMapper.getProjects(projectRequest, userId)).thenReturn(List.of(project));

        // Act
        PageResult<NoteItem> notePageResponsePage = noteService.getNotePageByProject(request, userId);

        // Assert
        verify(relationService, times(0)).getReferencedCountByReferencedNoteIds(noteIds, userId);
        assertThat(notePageResponsePage.getTotal()).isEqualTo(notePage.getTotal());
    }

    @Test
    void should_returnNotesPageByProject_whenViewOptionIsNotNull() {
        // Arrange
        String userId = "1";
        String projectId = "123";
        NotePageRequest request = new NotePageRequest();
        request.setObjectId(projectId);
        request.setViewType(0);
        request.setPageSize(10L);
        request.setPageIndex(1L);

        Page<Note> notePage = new Page<>();
        List<String> noteIds = List.of("1", "2", "3", "4", "5");
        List<Note> notes = constructNotesByNoteIds(noteIds, userId, projectId);
        notePage.setRecords(notes);
        notePage.setCurrent(request.getPageIndex());
        notePage.setSize(request.getPageSize());
        notePage.setTotal(notes.size());
        when(noteMapper.selectPage(any(), any())).thenReturn(notePage);

        ViewOption viewOption = new ViewOption();
        viewOption.setShowRelationNoteCount(1);
        viewOption.setOrderedBy(0);
        when(viewOptionService.getViewOption(any(ViewOption.class), any(String.class))).thenReturn(viewOption);

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectIds(Collections.singleton(projectId));
        Project project = new Project();
        project.setProjectId(projectId);
        project.setName("projectTestName");
        project.setCreator(userId);
        when(projectMapper.getProjects(projectRequest, userId)).thenReturn(List.of(project));

        // Act
        PageResult<NoteItem> notePageResponsePage = noteService.getNotePageByProject(request, userId);

        // Assert
        verify(relationService, times(1)).getReferencedCountByReferencedNoteIds(noteIds, userId);
        assertThat(notePageResponsePage.getTotal()).isEqualTo(notePage.getTotal());
    }

    @Test
    void should_returnNotesPageByLabel_whenViewOptionIsNull() {
        // Arrange
        String userId = "1";
        String labelId = "1";
        String projectId = "123";

        Page<Note> notePage = new Page<>();
        List<String> noteIds = List.of("1", "2", "3", "4", "5");
        List<Note> notes = constructNotesByNoteIds(noteIds, userId, projectId);
        notePage.setRecords(notes);
        notePage.setTotal(notes.size());
        when(noteMapper.selectPage(any(), any())).thenReturn(notePage);

        List<NoteLabelRelation> noteLabelRelations = new ArrayList<>();
        for (Note note: notes) {
            NoteLabelRelation noteLabelRelation = constructNoteLabelRelation(note.getNoteId(), labelId);
            noteLabelRelations.add(noteLabelRelation);
        }
        when(relationService.getNoteLabelRelations(anyString(), anyString())).thenReturn(noteLabelRelations);
        when(viewOptionService.getViewOption(any(ViewOption.class), anyString())).thenReturn(null);

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectIds(Collections.singleton(projectId));
        Project project = new Project();
        project.setProjectId(projectId);
        project.setName("projectTestName");
        project.setCreator(userId);
        when(projectMapper.getProjects(projectRequest, userId)).thenReturn(List.of(project));

        NotePageRequest request = new NotePageRequest();
        request.setObjectId(labelId);
        request.setViewType(5);
        request.setPageSize(10L);
        request.setPageIndex(1L);

        // Act
        PageResult<NoteItem> notePageResponsePage = noteService.getNotePageByLabel(request, userId);

        // Assert
        assertThat(notePageResponsePage.getTotal()).isEqualTo(notePage.getTotal());
    }

    @Test
    void should_returnNotesPageByLabel_whenViewOptionIsNotNull() {
        // Arrange
        String userId = "1";
        String labelId = "1";
        String projectId = "123";

        Page<Note> notePage = new Page<>();
        List<String> noteIds = List.of("1", "2", "3", "4", "5");
        List<Note> notes = constructNotesByNoteIds(noteIds, userId, projectId);
        notePage.setRecords(notes);
        notePage.setTotal(notes.size());
        when(noteMapper.selectPage(any(), any())).thenReturn(notePage);

        List<NoteLabelRelation> noteLabelRelations = new ArrayList<>();
        for (Note note: notes) {
            NoteLabelRelation noteLabelRelation = constructNoteLabelRelation(note.getNoteId(), labelId);
            noteLabelRelations.add(noteLabelRelation);
        }
        when(relationService.getNoteLabelRelations(anyString(), anyString())).thenReturn(noteLabelRelations);

        Project project = new Project();
        project.setProjectId(projectId);
        project.setName("projectTestName");
        project.setCreator(userId);
        when(projectMapper.getProjects(any(ProjectRequest.class), eq(userId))).thenReturn(List.of(project));

        ViewOption viewOption = new ViewOption();
        viewOption.setShowRelationNoteCount(1);
        viewOption.setViewType(5);
        viewOption.setOrderedBy(0);
        when(viewOptionService.getViewOption(any(ViewOption.class), anyString())).thenReturn(viewOption);


        NotePageRequest request = new NotePageRequest();
        request.setObjectId(labelId);
        request.setViewType(5);
        request.setPageSize(10L);
        request.setPageIndex(1L);

        // Act
        PageResult<NoteItem> notePageResponsePage = noteService.getNotePageByLabel(request, userId);

        // Assert
        verify(relationService, times(1)).getReferencedCountByReferencedNoteIds(noteIds, userId);
        assertThat(notePageResponsePage.getTotal()).isEqualTo(notePage.getTotal());
    }


    private List<Note> constructNotesByNoteIds(List<String> noteIds, String userId, String projectId) {
        List<Note> notes = new ArrayList<>();
        for (String noteId : noteIds) {
            notes.add(Note.builder()
                    .content("Hello World" + noteId + "!")
                    .creator(userId)
                    .updater(userId)
                    .noteId(noteId)
                    .isPinned(0)
                    .isFavorite(0)
                    .isArchived(0)
                    .isDeleted(0)
                    .projectId(projectId)
                    .build());
        }
        return notes;
    }

    private NoteItem constructNoteItem(Note note, String userId) {
        List<NoteItem> noteItems = new ArrayList<>();
        NoteItem noteItem = new NoteItem();
        noteItem.setNoteId(note.getNoteId());
        noteItem.setContent("Hello World!");
        noteItem.setCreator(userId);
        noteItem.setIsDeleted(note.getIsDeleted());
        noteItem.setIsArchived(note.getIsArchived());
        noteItem.setIsPinned(note.getIsPinned());
        return noteItem;
    }

    private NoteLabelRelation constructNoteLabelRelation(String noteId, String labelId) {
        return NoteLabelRelation.builder()
                .noteId(noteId)
                .labelId(labelId)
                .build();
    }
}

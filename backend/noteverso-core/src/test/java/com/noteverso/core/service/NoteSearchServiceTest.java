package com.noteverso.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.manager.NoteManager;
import com.noteverso.core.model.dto.LabelItem;
import com.noteverso.core.model.dto.NoteItem;
import com.noteverso.core.model.entity.Note;
import com.noteverso.core.model.entity.Project;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.request.ProjectRequest;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NoteSearchServiceTest {

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private RelationService relationService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private NoteManager noteManager;

    @InjectMocks
    private NoteServiceImpl noteService;

    @Test
    void should_searchNotes_byKeyword() {
        // Arrange
        String userId = "user123";
        String keyword = "meeting";
        
        Note note = createTestNote("note1", "Meeting notes", userId);
        List<Note> notes = List.of(note);
        
        Page<Note> page = new Page<>(1, 10);
        page.setRecords(notes);
        page.setTotal(1);

        when(noteMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(relationService.getReferencedCountByReferencedNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getReferencingCountByReferencingNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getAttachmentCountByObjectIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getLabelsByNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(projectMapper.getProjects(any(ProjectRequest.class), eq(userId))).thenReturn(new ArrayList<>());
        when(noteManager.constructNoteItem(any(), any(), any(), any(), any(), any())).thenReturn(createTestNoteItem("note1"));

        // Act
        PageResult<NoteItem> result = noteService.searchNotes(userId, keyword, null, null, null, null, "addedAt", "desc", 1, 10);

        // Assert
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getTotal()).isEqualTo(1);
    }

    @Test
    void should_searchNotes_byLabels() {
        // Arrange
        String userId = "user123";
        List<String> labelIds = List.of("label1", "label2");
        List<String> noteIds = List.of("note1", "note2");
        
        Note note = createTestNote("note1", "Test content", userId);
        List<Note> notes = List.of(note);
        
        Page<Note> page = new Page<>(1, 10);
        page.setRecords(notes);
        page.setTotal(1);

        when(relationService.getNoteIdsByLabelIds(labelIds, userId)).thenReturn(noteIds);
        when(noteMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(relationService.getReferencedCountByReferencedNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getReferencingCountByReferencingNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getAttachmentCountByObjectIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getLabelsByNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(projectMapper.getProjects(any(ProjectRequest.class), eq(userId))).thenReturn(new ArrayList<>());
        when(noteManager.constructNoteItem(any(), any(), any(), any(), any(), any())).thenReturn(createTestNoteItem("note1"));

        // Act
        PageResult<NoteItem> result = noteService.searchNotes(userId, null, labelIds, null, null, null, "addedAt", "desc", 1, 10);

        // Assert
        assertThat(result.getRecords()).hasSize(1);
    }

    @Test
    void should_searchNotes_byStatus_pinned() {
        // Arrange
        String userId = "user123";
        Integer status = 1; // Pinned
        
        Note note = createTestNote("note1", "Pinned note", userId);
        note.setIsPinned(1);
        List<Note> notes = List.of(note);
        
        Page<Note> page = new Page<>(1, 10);
        page.setRecords(notes);
        page.setTotal(1);

        when(noteMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(relationService.getReferencedCountByReferencedNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getReferencingCountByReferencingNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getAttachmentCountByObjectIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getLabelsByNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(projectMapper.getProjects(any(ProjectRequest.class), eq(userId))).thenReturn(new ArrayList<>());
        when(noteManager.constructNoteItem(any(), any(), any(), any(), any(), any())).thenReturn(createTestNoteItem("note1"));

        // Act
        PageResult<NoteItem> result = noteService.searchNotes(userId, null, null, status, null, null, "addedAt", "desc", 1, 10);

        // Assert
        assertThat(result.getRecords()).hasSize(1);
    }

    @Test
    void should_returnEmptyResult_whenNoNotesWithLabels() {
        // Arrange
        String userId = "user123";
        List<String> labelIds = List.of("label1");

        when(relationService.getNoteIdsByLabelIds(labelIds, userId)).thenReturn(new ArrayList<>());

        // Act
        PageResult<NoteItem> result = noteService.searchNotes(userId, null, labelIds, null, null, null, "addedAt", "desc", 1, 10);

        // Assert
        assertThat(result.getRecords()).isEmpty();
        assertThat(result.getTotal()).isEqualTo(0);
    }

    @Test
    void should_searchNotes_withSorting_ascending() {
        // Arrange
        String userId = "user123";
        
        Note note = createTestNote("note1", "Test", userId);
        List<Note> notes = List.of(note);
        
        Page<Note> page = new Page<>(1, 10);
        page.setRecords(notes);
        page.setTotal(1);

        when(noteMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);
        when(relationService.getReferencedCountByReferencedNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getReferencingCountByReferencingNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getAttachmentCountByObjectIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(relationService.getLabelsByNoteIds(any(), eq(userId))).thenReturn(new HashMap<>());
        when(projectMapper.getProjects(any(ProjectRequest.class), eq(userId))).thenReturn(new ArrayList<>());
        when(noteManager.constructNoteItem(any(), any(), any(), any(), any(), any())).thenReturn(createTestNoteItem("note1"));

        // Act
        PageResult<NoteItem> result = noteService.searchNotes(userId, null, null, null, null, null, "updatedAt", "asc", 1, 10);

        // Assert
        assertThat(result.getRecords()).hasSize(1);
    }

    private Note createTestNote(String noteId, String content, String userId) {
        Note note = new Note();
        note.setNoteId(noteId);
        note.setContentJson(Map.of("type", "doc", "content", List.of(Map.of("type", "paragraph", "content", List.of(Map.of("type", "text", "text", content))))));
        note.setCreator(userId);
        note.setProjectId("project1");
        note.setIsDeleted(0);
        note.setIsArchived(0);
        note.setIsPinned(0);
        note.setIsFavorite(0);
        note.setAddedAt(Instant.now());
        note.setUpdatedAt(Instant.now());
        return note;
    }

    private NoteItem createTestNoteItem(String noteId) {
        NoteItem item = new NoteItem();
        item.setNoteId(noteId);
        item.setContentJson(Map.of("type", "doc", "content", List.of(Map.of("type", "paragraph", "content", List.of(Map.of("type", "text", "text", "Test content"))))));
        item.setAddedAt(Instant.now().toString());
        item.setUpdatedAt(Instant.now().toString());
        return item;
    }
}

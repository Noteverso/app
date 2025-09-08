package com.noteverso.core.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noteverso.common.exceptions.DuplicateRecordException;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.core.dao.LabelMapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.model.dto.LabelItem;
import com.noteverso.core.manager.NoteManager;
import com.noteverso.core.model.dto.NoteItem;
import com.noteverso.core.model.entity.*;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.request.LabelCreateRequest;
import com.noteverso.core.model.request.LabelUpdateRequest;
import com.noteverso.core.model.request.NotePageRequest;
import com.noteverso.core.model.request.ProjectRequest;
import com.noteverso.core.service.impl.LabelServiceImpl;
import com.noteverso.core.service.impl.ViewOptionServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LabelServiceTest {
    @Mock
    private LabelMapper labelMapper;

    @Mock
    private NoteManager noteManager;

    @Mock
    private NoteLabelRelationMapper noteLabelRelationMapper;

    @Mock
    private RelationService relationService;

    @Mock
    private ViewOptionServiceImpl viewOptionService;

    @InjectMocks
    private LabelServiceImpl labelService;

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Test
    void should_createLabelSuccessfully() {
        // Arrange
        String userId = "1234567";
        LabelCreateRequest request = new LabelCreateRequest();
        request.setName("test");
        request.setIsFavorite(0);
        request.setColor("red");

        // Act
        labelService.createLabel(request, userId);

        // Assert
        ArgumentCaptor<Label> captor = ArgumentCaptor.forClass(Label.class);
        verify(labelMapper).insert(captor.capture());
        Label newedlabel = captor.getValue();
        assertEquals(newedlabel.getName(), request.getName());
    }

    @Test
    void createLabel_shouldThrowException_whenNameDuplicate() {
        // Arrange
        String userId = "1234567";
        LabelCreateRequest request = new LabelCreateRequest();
        request.setName("test");
        request.setIsFavorite(0);
        request.setColor("red");
        doThrow(new DuplicateKeyException("")).when(labelMapper).insert(any());

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> labelService.createLabel(request, userId);

        // Assert
        assertThatThrownBy(callable)
                .isInstanceOf(DuplicateRecordException.class)
                .hasMessage("Label name has already been taken");
    }

    @Test
    void should_updateLabelSuccessfully() {
        // Arrange
        LabelUpdateRequest request = new LabelUpdateRequest();
        request.setName("test");
        request.setIsFavorite(1);
        request.setColor("blue");

        // Act
        labelService.updateLabel("1", request, "1234567");

        // Assert
        ArgumentCaptor<Label> captor = ArgumentCaptor.forClass(Label.class);
        verify(labelMapper).update(captor.capture(), any());
        Label updatedLabel = captor.getValue();
        assertEquals(updatedLabel.getName(), request.getName());
    }

    @Test
    void should_deleteLabelSuccessfully() {
        // Arrange
        String labelId = "1";
        String userId = "1234567";
        when(labelMapper.delete(any())).thenReturn(1);

        // Act
        labelService.deleteLabel(labelId, userId);

        // Assert
        verify(noteLabelRelationMapper, times(1)).delete(any());
    }

    @Test
    void deleteLabel_shouldThrowException_whenLabelNotFound() {
        // Arrange
        String labelId = "1";
        String userId = "1234567";

        when(labelMapper.delete(any())).thenReturn(0);

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> labelService.deleteLabel(labelId, userId);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Label not found");
    }

    @Test
    void updateIsFavoriteStatus_shouldThrowException_whenLabelNotFound() {
        // Arrange
        String labelId = "1";

        /* Act */ThrowableAssert.ThrowingCallable callable = () -> labelService.updateIsFavoriteStatus(labelId, 1);
        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Label not found");
    }

    @Test
    void should_getLabelsSuccessfully() {
        // Arrange
        String userId = "1234567";
        String labelId1 = "1";
        String labelId2 = "2";
        List<Label> labels = List.of(
                constructLabel(labelId1, "test1", "red", userId),
                constructLabel(labelId2, "test2", "blue", userId)
        );

        HashMap<String, Long> noteCountMap = new HashMap<>();
        noteCountMap.put(labelId1, 1L);
        noteCountMap.put(labelId2, 2L);

        when(labelMapper.selectList(any())).thenReturn(labels);
        when(relationService.getNoteCountByLabels(any(List.class), any(String.class))).thenReturn(noteCountMap);

        // Act
        List<LabelItem> labelItems = labelService.getLabels(userId);

        // Assert
        assertThat(labelItems).hasSize(labels.size());
        assertThat(labelItems.get(0).getLabelId()).isEqualTo(labelId1);
    }

    private Label constructLabel(String labelId, String name, String color, String userId) {
        return Label.builder()
                .labelId(labelId).name(name).color(color)
                .creator(userId).updater(userId)
                .addedAt(Instant.now()).updatedAt(Instant.now())
                .isFavorite(0)
                .build();
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


        List<NoteItem> noteItems = new ArrayList<>();
        for (Note note : notes) {
            NoteItem noteItem = new NoteItem();
            noteItem.setNoteId(note.getNoteId());
            noteItems.add(noteItem);
        }

        PageResult<NoteItem> responsePage = new PageResult<>();
        responsePage.setRecords(noteItems);
        responsePage.setTotal(notePage.getTotal());
        responsePage.setPageIndex(notePage.getCurrent());
        responsePage.setPageSize(notePage.getSize());
        ViewOption viewOption = null;
        when(noteManager.getNoteItemPage(notePage, viewOption, userId)).thenReturn(responsePage);

        NotePageRequest request = new NotePageRequest();
        request.setObjectId(labelId);
        request.setViewType(5);
        request.setPageSize(10L);
        request.setPageIndex(1L);

        // Act
        PageResult<NoteItem> notePageResponsePage = labelService.getNotePageByLabel(labelId, request, userId);

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

        List<NoteItem> noteItems = new ArrayList<>();
        for (Note note : notes) {
            NoteItem noteItem = new NoteItem();
            noteItem.setNoteId(note.getNoteId());
            noteItems.add(noteItem);
        }

        ViewOption viewOption = new ViewOption();
        viewOption.setShowRelationNoteCount(1);
        viewOption.setViewType(5);
        viewOption.setOrderedBy(0);
        when(viewOptionService.getViewOption(any(ViewOption.class), anyString())).thenReturn(viewOption);

        PageResult<NoteItem> responsePage = new PageResult<>();
        responsePage.setRecords(noteItems);
        responsePage.setTotal(notePage.getTotal());
        responsePage.setPageIndex(notePage.getCurrent());
        responsePage.setPageSize(notePage.getSize());
        when(noteManager.getNoteItemPage(notePage, viewOption, userId)).thenReturn(responsePage);

        NotePageRequest request = new NotePageRequest();
        request.setObjectId(labelId);
        request.setViewType(5);
        request.setPageSize(10L);
        request.setPageIndex(1L);

        // Act
        PageResult<NoteItem> notePageResponsePage = labelService.getNotePageByLabel(labelId, request, userId);

        // Assert
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

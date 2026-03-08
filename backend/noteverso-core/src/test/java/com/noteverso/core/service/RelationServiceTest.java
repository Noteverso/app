package com.noteverso.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.core.dao.AttachmentRelationMapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.dao.NoteRelationMapper;
import com.noteverso.core.model.dto.AttachmentCount;
import com.noteverso.core.model.dto.LabelItem;
import com.noteverso.core.model.dto.ReferencedNoteCount;
import com.noteverso.core.model.dto.ReferencingNoteCount;
import com.noteverso.core.model.entity.AttachmentRelation;
import com.noteverso.core.model.entity.NoteLabelRelation;
import com.noteverso.core.service.impl.RelationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelationServiceTest {

    @Mock
    private NoteLabelRelationMapper noteLabelRelationMapper;

    @Mock
    private NoteRelationMapper noteRelationMapper;

    @Mock
    private AttachmentRelationMapper attachmentRelationMapper;

    @InjectMocks
    private RelationServiceImpl relationService;

    @Test
    void should_getNoteIdsByLabelIds_successfully() {
        // Arrange
        String userId = "user123";
        List<String> labelIds = List.of("label1", "label2");

        NoteLabelRelation relation1 = new NoteLabelRelation();
        relation1.setNoteId("note1");
        relation1.setLabelId("label1");

        NoteLabelRelation relation2 = new NoteLabelRelation();
        relation2.setNoteId("note2");
        relation2.setLabelId("label1");

        NoteLabelRelation relation3 = new NoteLabelRelation();
        relation3.setNoteId("note1");
        relation3.setLabelId("label2");

        List<NoteLabelRelation> relations = List.of(relation1, relation2, relation3);

        when(noteLabelRelationMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(relations);

        // Act
        List<String> noteIds = relationService.getNoteIdsByLabelIds(labelIds, userId);

        // Assert
        assertThat(noteIds).hasSize(2);
        assertThat(noteIds).containsExactlyInAnyOrder("note1", "note2");
    }

    @Test
    void should_returnEmptyList_whenNoLabels() {
        // Arrange
        String userId = "user123";
        List<String> labelIds = new ArrayList<>();

        // Act
        List<String> noteIds = relationService.getNoteIdsByLabelIds(labelIds, userId);

        // Assert
        assertThat(noteIds).isEmpty();
    }

    @Test
    void should_getLabelsByNoteIds_returnEmpty_whenEmptyList() {
        // Arrange
        String userId = "user123";
        List<String> emptyNoteIds = List.of();

        // Act
        HashMap<String, List<LabelItem>> labelMap = relationService.getLabelsByNoteIds(emptyNoteIds, userId);

        // Assert
        assertThat(labelMap).isEmpty();
    }

    @Test
    void should_getLabelsByNoteIds_returnEmpty_whenNull() {
        // Arrange
        String userId = "user123";

        // Act
        HashMap<String, List<LabelItem>> labelMap = relationService.getLabelsByNoteIds(null, userId);

        // Assert
        assertThat(labelMap).isEmpty();
    }

    @Test
    void should_returnEmptyList_whenNoRelations() {
        // Arrange
        String userId = "user123";
        List<String> labelIds = List.of("label1");

        when(noteLabelRelationMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());

        // Act
        List<String> noteIds = relationService.getNoteIdsByLabelIds(labelIds, userId);

        // Assert
        assertThat(noteIds).isEmpty();
    }

    @Test
    void should_removeDuplicateNoteIds() {
        // Arrange
        String userId = "user123";
        List<String> labelIds = List.of("label1", "label2");

        NoteLabelRelation relation1 = new NoteLabelRelation();
        relation1.setNoteId("note1");
        relation1.setLabelId("label1");

        NoteLabelRelation relation2 = new NoteLabelRelation();
        relation2.setNoteId("note1");
        relation2.setLabelId("label2");

        List<NoteLabelRelation> relations = List.of(relation1, relation2);

        when(noteLabelRelationMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(relations);

        // Act
        List<String> noteIds = relationService.getNoteIdsByLabelIds(labelIds, userId);

        // Assert
        assertThat(noteIds).hasSize(1);
        assertThat(noteIds).containsExactly("note1");
    }

    @Test
    void getAttachmentsByNoteId() {
        // Arrange
        String noteId = "note1";
        String userId = "user1";
        
        AttachmentRelation relation1 = new AttachmentRelation();
        relation1.setAttachmentId("att1");
        relation1.setObjectId(noteId);
        
        AttachmentRelation relation2 = new AttachmentRelation();
        relation2.setAttachmentId("att2");
        relation2.setObjectId(noteId);
        
        when(attachmentRelationMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(List.of(relation1, relation2));
        
        // Act
        List<String> attachmentIds = relationService.getAttachmentsByNoteId(noteId, userId);
        
        // Assert
        assertThat(attachmentIds).hasSize(2);
        assertThat(attachmentIds).containsExactlyInAnyOrder("att1", "att2");
    }

    @Test
    void getLabelsByNoteId() {
        // Arrange
        String noteId = "note1";
        String userId = "user1";
        
        NoteLabelRelation relation1 = new NoteLabelRelation();
        relation1.setLabelId("label1");
        relation1.setNoteId(noteId);
        
        NoteLabelRelation relation2 = new NoteLabelRelation();
        relation2.setLabelId("label2");
        relation2.setNoteId(noteId);
        
        when(noteLabelRelationMapper.selectList(any(LambdaQueryWrapper.class)))
            .thenReturn(List.of(relation1, relation2));
        
        // Act
        List<String> labelIds = relationService.getLabelsByNoteId(noteId, userId);
        
        // Assert
        assertThat(labelIds).hasSize(2);
        assertThat(labelIds).containsExactlyInAnyOrder("label1", "label2");
    }

    @Test
    void getAttachmentCountByObjectIds() {
        // Arrange
        String userId = "user1";
        List<String> objectIds = List.of("note1", "note2");
        
        AttachmentCount count1 = new AttachmentCount();
        count1.setObjectId("note1");
        count1.setAttachmentCount(3L);
        
        AttachmentCount count2 = new AttachmentCount();
        count2.setObjectId("note2");
        count2.setAttachmentCount(5L);
        
        when(attachmentRelationMapper.getAttachmentCountByObjectIds(objectIds, userId))
            .thenReturn(List.of(count1, count2));
        
        // Act
        HashMap<String, Long> countMap = relationService.getAttachmentCountByObjectIds(objectIds, userId);
        
        // Assert
        assertThat(countMap).hasSize(2);
        assertThat(countMap.get("note1")).isEqualTo(3L);
        assertThat(countMap.get("note2")).isEqualTo(5L);
    }

    @Test
    void getReferencedCountByReferencedNoteIds() {
        // Arrange
        String userId = "user1";
        List<String> referencedNoteIds = List.of("note1", "note2");
        
        ReferencedNoteCount count1 = new ReferencedNoteCount();
        count1.setReferencedNoteId("note1");
        count1.setReferencedNoteCount(2L);
        
        ReferencedNoteCount count2 = new ReferencedNoteCount();
        count2.setReferencedNoteId("note2");
        count2.setReferencedNoteCount(4L);
        
        when(noteRelationMapper.getReferencedNoteCountByReferencedIds(referencedNoteIds, userId))
            .thenReturn(List.of(count1, count2));
        
        // Act
        HashMap<String, Long> countMap = relationService.getReferencedCountByReferencedNoteIds(referencedNoteIds, userId);
        
        // Assert
        assertThat(countMap).hasSize(2);
        assertThat(countMap.get("note1")).isEqualTo(2L);
        assertThat(countMap.get("note2")).isEqualTo(4L);
    }

    @Test
    void getReferencingCountByReferencingNoteIds() {
        // Arrange
        String userId = "user1";
        List<String> referencingNoteIds = List.of("note1", "note2");
        
        ReferencingNoteCount count1 = new ReferencingNoteCount();
        count1.setReferencingNoteId("note1");
        count1.setReferencingNoteCount(3L);
        
        ReferencingNoteCount count2 = new ReferencingNoteCount();
        count2.setReferencingNoteId("note2");
        count2.setReferencingNoteCount(1L);
        
        when(noteRelationMapper.getReferencingNoteCountByReferencingIds(referencingNoteIds, userId))
            .thenReturn(List.of(count1, count2));
        
        // Act
        HashMap<String, Long> countMap = relationService.getReferencingCountByReferencingNoteIds(referencingNoteIds, userId);
        
        // Assert
        assertThat(countMap).hasSize(2);
        assertThat(countMap.get("note1")).isEqualTo(3L);
        assertThat(countMap.get("note2")).isEqualTo(1L);
    }

    @Test
    void should_getAttachmentCountByObjectIds_returnEmpty_whenEmptyList() {
        // Arrange
        String userId = "user1";
        List<String> emptyList = List.of();
        
        // Act
        HashMap<String, Long> countMap = relationService.getAttachmentCountByObjectIds(emptyList, userId);
        
        // Assert
        assertThat(countMap).isEmpty();
    }

    @Test
    void should_getReferencedCountByReferencedNoteIds_returnEmpty_whenEmptyList() {
        // Arrange
        String userId = "user1";
        List<String> emptyList = List.of();
        
        // Act
        HashMap<String, Long> countMap = relationService.getReferencedCountByReferencedNoteIds(emptyList, userId);
        
        // Assert
        assertThat(countMap).isEmpty();
    }

    @Test
    void should_getReferencingCountByReferencingNoteIds_returnEmpty_whenEmptyList() {
        // Arrange
        String userId = "user1";
        List<String> emptyList = List.of();
        
        // Act
        HashMap<String, Long> countMap = relationService.getReferencingCountByReferencingNoteIds(emptyList, userId);
        
        // Assert
        assertThat(countMap).isEmpty();
    }

    @Test
    void should_getNoteCountByLabels_returnEmpty_whenEmptyList() {
        // Arrange
        String userId = "user1";
        List<String> emptyList = List.of();
        
        // Act
        HashMap<String, Long> countMap = relationService.getNoteCountByLabels(emptyList, userId);
        
        // Assert
        assertThat(countMap).isEmpty();
    }

    @Test
    void should_insertNoteLabelRelation_notCallMapper_whenEmptyList() {
        // Arrange
        List<String> emptyLabels = List.of();
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.insertNoteLabelRelation(emptyLabels, noteId, userId);

        // Assert
        assertThat(emptyLabels).isEmpty();
    }

    @Test
    void should_insertNoteLabelRelation_notCallMapper_whenNull() {
        // Arrange
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.insertNoteLabelRelation(null, noteId, userId);

        // Assert - no exception thrown
    }

    @Test
    void should_insertNoteAttachmentRelation_notCallMapper_whenEmptyList() {
        // Arrange
        List<String> emptyAttachments = List.of();
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.insertNoteAttachmentRelation(emptyAttachments, noteId, userId);

        // Assert
        assertThat(emptyAttachments).isEmpty();
    }

    @Test
    void should_insertNoteAttachmentRelation_notCallMapper_whenNull() {
        // Arrange
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.insertNoteAttachmentRelation(null, noteId, userId);

        // Assert - no exception thrown
    }

    @Test
    void should_insertNoteRelation_notCallMapper_whenEmptyList() {
        // Arrange
        List<String> emptyLinkedNotes = List.of();
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.insertNoteRelation(emptyLinkedNotes, noteId, userId);

        // Assert
        assertThat(emptyLinkedNotes).isEmpty();
    }

    @Test
    void should_insertNoteRelation_notCallMapper_whenNull() {
        // Arrange
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.insertNoteRelation(null, noteId, userId);

        // Assert - no exception thrown
    }

    @Test
    void should_updateNoteRelation_notCallMapper_whenEmptyList() {
        // Arrange
        List<String> emptyLinkedNotes = List.of();
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.updateNoteRelation(emptyLinkedNotes, noteId, userId);

        // Assert
        assertThat(emptyLinkedNotes).isEmpty();
    }

    @Test
    void should_updateNoteRelation_notCallMapper_whenNull() {
        // Arrange
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.updateNoteRelation(null, noteId, userId);

        // Assert - no exception thrown
    }

    @Test
    void should_updateNoteLabelRelation_notCallMapper_whenEmptyList() {
        // Arrange
        List<String> emptyLabels = List.of();
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.updateNoteLabelRelation(emptyLabels, noteId, userId);

        // Assert
        assertThat(emptyLabels).isEmpty();
    }

    @Test
    void should_updateNoteLabelRelation_notCallMapper_whenNull() {
        // Arrange
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.updateNoteLabelRelation(null, noteId, userId);

        // Assert - no exception thrown
    }

    @Test
    void should_updateNoteAttachment_notCallMapper_whenEmptyList() {
        // Arrange
        List<String> emptyAttachments = List.of();
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.updateNoteAttachment(emptyAttachments, noteId, userId);

        // Assert
        assertThat(emptyAttachments).isEmpty();
    }

    @Test
    void should_updateNoteAttachment_notCallMapper_whenNull() {
        // Arrange
        String noteId = "note1";
        String userId = "user1";

        // Act
        relationService.updateNoteAttachment(null, noteId, userId);

        // Assert - no exception thrown
    }
}
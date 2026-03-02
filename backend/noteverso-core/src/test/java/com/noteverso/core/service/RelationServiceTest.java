package com.noteverso.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.model.entity.NoteLabelRelation;
import com.noteverso.core.service.impl.RelationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelationServiceTest {

    @Mock
    private NoteLabelRelationMapper noteLabelRelationMapper;

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
    }

    @Test
    void getLabelsByNoteId() {
    }

    @Test
    void getAttachmentCountByObjectIds() {
    }

    @Test
    void getReferencedCountByReferencedNoteIds() {
    }

    @Test
    void getReferencingCountByReferencingNoteIds() {
    }
}
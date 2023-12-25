package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.dto.ReferencedNoteCount;
import com.noteverso.core.dto.ReferencingNoteCount;
import com.noteverso.core.model.NoteRelation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NoteRelationMapperTest {
    @Autowired
    NoteRelationMapper noteRelationMapper;

    @Test
    void getReferencedNotesCountByReferencingIds() {
        // Arrange
        String userId = "1";
        List<String> referencedNoteIds = List.of("1", "2", "3", "4", "5");
        List<NoteRelation> referencedNoteRelations = new ArrayList<>();
        for (String referencedNoteId : referencedNoteIds) {
            Instant now = Instant.now();
            NoteRelation relation = new NoteRelation();
            relation.setNoteId("123");
            relation.setLinkedNoteId(referencedNoteId);
            relation.setCreator(userId);
            relation.setUpdater(userId);
            relation.setAddedAt(now);
            relation.setUpdatedAt(now);
            referencedNoteRelations.add(relation);
        }
        noteRelationMapper.batchInsert(referencedNoteRelations);

        // Act
        List<ReferencingNoteCount> result = noteRelationMapper.getReferencingNoteCountByReferencingIds(List.of("123"), userId);

        // Assert
        ReferencingNoteCount resultMap = result.get(0);
        assertThat(result).hasSize(1);
        assertThat(resultMap.getReferencingNoteId()).isEqualTo("123");
        assertThat(resultMap.getReferencingNoteCount()).isEqualTo(referencedNoteIds.size());
    }

    @Test
    void getReferencingNotesCountByReferencedIds() {
        // Arrange
        String userId = "1";
        List<String> referencingNoteIds = List.of("5", "6", "7", "8", "9", "10");
        List<NoteRelation> referencingNoteRelations = new ArrayList<>();
        for (String referencingNoteId : referencingNoteIds) {
            Instant now = Instant.now();
            NoteRelation relation = new NoteRelation();
            relation.setNoteId(referencingNoteId);
            relation.setLinkedNoteId("123");
            relation.setCreator(userId);
            relation.setUpdater(userId);
            relation.setAddedAt(now);
            relation.setUpdatedAt(now);
            referencingNoteRelations.add(relation);
        }
        noteRelationMapper.batchInsert(referencingNoteRelations);

        // Act
        List<ReferencedNoteCount> result = noteRelationMapper.getReferencedNoteCountByReferencedIds(List.of("123"), userId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReferencedNoteId()).isEqualTo("123");
        assertThat(result.get(0).getReferencedNoteCount()).isEqualTo(referencingNoteIds.size());
    }
}
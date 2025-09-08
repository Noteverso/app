package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.dto.NoteCountForLabel;
import com.noteverso.core.model.entity.NoteLabelRelation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NoteLabelRelationMapperTest {
    @Autowired
    NoteLabelRelationMapper noteLabelRelationMapper;

    @Test
    void should_returnNoteCount_when_getNoteCountByLabels() {
        // Arrange
        List<NoteLabelRelation> noteLabelRelations = List.of(
                constructNoteLabelRelation("1", "1", "1"),
                constructNoteLabelRelation("1", "2", "1"),
                constructNoteLabelRelation("2", "1", "1"),
                constructNoteLabelRelation("2", "2", "1")
        );

        noteLabelRelationMapper.batchInsert(noteLabelRelations);

        // Act
        List<NoteCountForLabel> noteCountForLabels = noteLabelRelationMapper.getNoteCountByLabels(List.of("1", "2"), "1");

        // Assert
        assertEquals(2, noteCountForLabels.size());
        assertEquals(2, noteCountForLabels.get(0).getNoteCount());
    }

    private NoteLabelRelation constructNoteLabelRelation(String labelId, String noteId, String userId) {
        NoteLabelRelation noteLabelRelation = new NoteLabelRelation();
        noteLabelRelation.setLabelId(labelId);
        noteLabelRelation.setNoteId(noteId);
        noteLabelRelation.setCreator(userId);
        noteLabelRelation.setUpdater(userId);
        noteLabelRelation.setUpdatedAt(Instant.now());
        noteLabelRelation.setAddedAt(Instant.now());
        return noteLabelRelation;
    }
}

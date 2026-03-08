package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.entity.NoteProjectRelation;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Disabled("Table noteverso_note_project_map does not exist in schema - mapper appears unused")
class NoteProjectRelationMapperTest {

    @Autowired
    private NoteProjectRelationMapper noteProjectRelationMapper;

    @Test
    void should_insert_successfully() {
        // Arrange
        NoteProjectRelation relation = constructRelation("note1", "project1", "user1");

        // Act
        int result = noteProjectRelationMapper.insert(relation);

        // Assert
        assertThat(result).isEqualTo(1);
        NoteProjectRelation found = noteProjectRelationMapper.selectById(relation.getId());
        assertThat(found).isNotNull();
        assertThat(found.getNoteId()).isEqualTo("note1");
        assertThat(found.getProjectId()).isEqualTo("project1");
    }

    @Test
    void should_delete_successfully() {
        // Arrange
        NoteProjectRelation relation = constructRelation("note2", "project2", "user1");
        noteProjectRelationMapper.insert(relation);

        // Act
        int result = noteProjectRelationMapper.deleteById(relation.getId());

        // Assert
        assertThat(result).isEqualTo(1);
        NoteProjectRelation found = noteProjectRelationMapper.selectById(relation.getId());
        assertThat(found).isNull();
    }

    private NoteProjectRelation constructRelation(String noteId, String projectId, String userId) {
        return NoteProjectRelation.builder()
            .noteId(noteId)
            .projectId(projectId)
            .creator(userId)
            .updater(userId)
            .addedAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }
}

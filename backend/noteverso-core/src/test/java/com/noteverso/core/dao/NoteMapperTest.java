package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.dto.NoteCountForProject;
import com.noteverso.core.model.dto.ProjectViewOption;
import com.noteverso.core.model.entity.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, statements = "DELETE FROM noteverso_note")
class NoteMapperTest {
    @Autowired
    NoteMapper noteMapper;

    @Test
    public void saveAndFindNote_shouldReturnSpecificNote() {
        String noteId = "123456789";
        String content = "Hello World!";
        String projectId = "123456789";
        String userId = "123456789";
        Note note = buildNote(noteId, content, projectId, userId);
        noteMapper.insertWithJsonb(note);

        Note findedNote = noteMapper.selectByNoteId(noteId, 0);
        assertThat(findedNote.getNoteId()).isEqualTo(noteId);
    }

    @Test
    void should_archiveNotesSuccessfully_withProjectId() {
        // Arrange
        String noteId1 = "1";
        String noteId2 = "2";
        String content = "Hello World!";
        String projectId = "123456789";
        String userId = "123456789";

        Note note1 = buildNote(noteId1, content, projectId, userId);
        Note note2 = buildNote(noteId2, content, projectId, userId);

        noteMapper.insertWithJsonb(note1);
        noteMapper.insertWithJsonb(note2);

        // Act
        noteMapper.updateNoteIsArchivedByProject(projectId, userId, 1);

        // Assert
        Note archivedNote1 = noteMapper.selectByNoteId(noteId1, 0);
        Note archivedNote2 = noteMapper.selectByNoteId(noteId2, 0);
        assertThat(archivedNote1.getIsArchived()).isEqualTo(1);
        assertThat(archivedNote2.getIsArchived()).isEqualTo(1);
    }

    @Test
    void should_updateNotesIsDeletedSuccessfully_withProjectId() {
        // Arrange
        String noteId1 = "1";
        String noteId2 = "2";
        String content = "Hello World!";
        String projectId = "123456789";
        String userId = "123456789";

        Note note1 = buildNote(noteId1, content, projectId, userId);
        Note note2 = buildNote(noteId2, content, projectId, userId);

        noteMapper.insertWithJsonb(note1);
        noteMapper.insertWithJsonb(note2);

        // Act
        noteMapper.updateNotesIsDeletedByProject(projectId, userId);

        // Assert
        Note deletedNote1 = noteMapper.selectByNoteId(noteId1, 1);
        Note deletedNote2 = noteMapper.selectByNoteId(noteId2, 1);
        assertThat(deletedNote1.getIsDeleted()).isEqualTo(1);
        assertThat(deletedNote2.getIsDeleted()).isEqualTo(1);
    }

    @Test
    void should_returnNotes_whenBatchSelectByNoteIds() {
        // Arrange
        String noteId1 = "1";
        String noteId2 = "2";
        String content = "Hello World!";
        String projectId = "123456789";
        String userId = "123456789";

        Note note1 = buildNote(noteId1, content, projectId, userId);
        Note note2 = buildNote(noteId2, content, projectId, userId);

        noteMapper.insertWithJsonb(note1);
        noteMapper.insertWithJsonb(note2);

        // Act
        List<Note> notes = noteMapper.batchSelectByNoteIds(List.of(noteId1, noteId2), userId, 0);

        // Assert
        assertThat(notes).hasSize(2);
    }

    @Test
    void should_returnNoteCount_whenNotShowArchivedAndDeletedNotes() {
        // Arrange
        Note note1 = buildNote("1", "Hello World1!", "123", "123456789");
        Note note2 = buildNote("2", "Hello World2!", "123", "123456789");
        Note note3 = buildNote("3", "Hello World3!", "123", "123456789");
        note3.setIsArchived(1);
        Note note4 = buildNote("4", "Hello World4!", "456", "123456789");
        Note note5 = buildNote("5", "Hello World5!", "456", "123456789");
        Note note6 = buildNote("6", "Hello World6!", "456", "123456789");
        note6.setIsDeleted(1);
        noteMapper.insertWithJsonb(note1);
        noteMapper.insertWithJsonb(note2);
        noteMapper.insertWithJsonb(note3);
        noteMapper.insertWithJsonb(note4);
        noteMapper.insertWithJsonb(note5);
        noteMapper.insertWithJsonb(note6);
        List<ProjectViewOption> projectViewOptions = List.of(
            new ProjectViewOption("123", 0, 0),
            new ProjectViewOption("456", 0, 0)
        );

        // Act
        List<NoteCountForProject> result = noteMapper.getNoteCountByProjects(projectViewOptions, "123456789");

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNoteCount()).isEqualTo(2);
    }

    @Test
    void should_returnNote_whenShowArchivedAndDeletedNotes() {
        // Arrange
        Note note1 = buildNote("1", "Hello World1!", "123", "123456789");
        Note note2 = buildNote("2", "Hello World2!", "123", "123456789");
        note2.setIsArchived(1);
        Note note3 = buildNote("3", "Hello World3!", "123", "123456789");
        note3.setIsDeleted(1);
        noteMapper.insertWithJsonb(note1);
        noteMapper.insertWithJsonb(note2);
        noteMapper.insertWithJsonb(note3);

        List<ProjectViewOption> projectViewOptions = List.of(
                new ProjectViewOption("123", 1, 1)
        );

        // Act
        List<NoteCountForProject> result = noteMapper.getNoteCountByProjects(projectViewOptions, "123456789");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNoteCount()).isEqualTo(3);
    }

    @Test
    void should_getNoteCountByProjects_returnEmpty_whenEmptyList() {
        // Arrange
        String userId = "123456789";
        List<ProjectViewOption> emptyList = List.of();

        // Act
        List<NoteCountForProject> result = noteMapper.getNoteCountByProjects(emptyList, userId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void should_batchSelectByNoteIds_returnEmpty_whenEmptyList() {
        // Arrange
        String userId = "123456789";
        List<String> emptyList = List.of();

        // Act
        List<Note> result = noteMapper.batchSelectByNoteIds(emptyList, userId, 0);

        // Assert
        assertThat(result).isEmpty();
    }

    private Note buildNote(String noteId, String content, String projectId, String userId) {
        // Create simple JSON content for tests
        Object contentJson = Map.of(
            "type", "doc",
            "content", List.of(Map.of(
                "type", "paragraph",
                "content", List.of(Map.of(
                    "type", "text",
                    "text", content
                ))
            ))
        );
        
        return Note.builder()
                .noteId(noteId)
                .contentJson(Map.of("type", "doc", "content", List.of(Map.of("type", "paragraph", "content", List.of(Map.of("type", "text", "text", content))))))
                .contentJson(contentJson)
                .projectId(projectId)
                .creator(userId)
                .updater(userId)
                .addedAt(Instant.now())
                .isArchived(0)
                .isDeleted(0)
                .updatedAt(Instant.now())
                .build();
    }
}

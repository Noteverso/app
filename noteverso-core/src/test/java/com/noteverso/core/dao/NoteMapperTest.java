package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.dto.NoteCountForProject;
import com.noteverso.core.dto.ProjectViewOption;
import com.noteverso.core.model.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
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
        noteMapper.insert(note);

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

        noteMapper.insert(note1);
        noteMapper.insert(note2);

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

        noteMapper.insert(note1);
        noteMapper.insert(note2);

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

        noteMapper.insert(note1);
        noteMapper.insert(note2);

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
        noteMapper.insert(note1);
        noteMapper.insert(note2);
        noteMapper.insert(note3);
        noteMapper.insert(note4);
        noteMapper.insert(note5);
        noteMapper.insert(note6);
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
        noteMapper.insert(note1);
        noteMapper.insert(note2);
        noteMapper.insert(note3);

        List<ProjectViewOption> projectViewOptions = List.of(
                new ProjectViewOption("123", 1, 1)
        );

        // Act
        List<NoteCountForProject> result = noteMapper.getNoteCountByProjects(projectViewOptions, "123456789");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNoteCount()).isEqualTo(3);
    }

    private Note buildNote(String noteId, String content, String projectId, String userId) {
        return Note.builder()
                .noteId(noteId)
                .content(content)
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
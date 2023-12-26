package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
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
        List<Note> notes = noteMapper.batchSelect(List.of(noteId1, noteId2), userId, 0);

        // Assert
        assertThat(notes).hasSize(2);
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
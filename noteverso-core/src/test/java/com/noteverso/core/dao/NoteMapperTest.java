package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;

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
        String tenantId = "123456789";
        Note note = Note.builder()
                .noteId(noteId).content(content).projectId(projectId).creator(tenantId).updater(tenantId).addedAt(Instant.now()).updatedAt(Instant.now())
                .build();
        noteMapper.insert(note);

        Note findedNote = noteMapper.selectByNoteId(noteId);
        assertThat(findedNote.getNoteId()).isEqualTo(noteId);
    }
}
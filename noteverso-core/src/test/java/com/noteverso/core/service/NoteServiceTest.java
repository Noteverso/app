package com.noteverso.core.service;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.common.exceptions.BaseException;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.model.Note;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;


import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class NoteServiceTest {
    @Autowired
    NoteMapper noteMapper;


    @Test
    @DisplayName("Test should Pass When Comment do not contain Swear Words")
    public void shouldNotContainSwearWords() {
        NoteService noteService = new NoteServiceImpl(null, null, null, null);
        boolean containsSwearWords = noteService.containsSwearWords("test");
        assertFalse(containsSwearWords);
        assertThat(containsSwearWords).isFalse();
    }

    @Test
    @DisplayName("Should Throw Exception when Exception Contains Swear Words")
    public void shouldThrowExceptionWhenContainsSwearWords() {
        NoteService noteService = new NoteServiceImpl(null, null, null, null);
        BaseException exception = assertThrows(BaseException.class, () -> {
            noteService.containsSwearWords("This is shitty comment");
        });
        assertTrue(exception.getMessage().contains("Comments contains unacceptable language"));
        assertThatThrownBy(() -> {
            noteService.containsSwearWords("This is shitty comment");
        }).isInstanceOf(BaseException.class).hasMessage("Comments contains unacceptable language");
    }

    @Test
    @DisplayName("Test should Pass When Insert Note")
    public void shouldInsertNote() {
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

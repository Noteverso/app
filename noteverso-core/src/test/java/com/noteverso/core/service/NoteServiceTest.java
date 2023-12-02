package com.noteverso.core.service;

import com.noteverso.common.exceptions.BaseException;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class NoteServiceTest {
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
}

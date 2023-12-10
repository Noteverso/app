package com.noteverso.core.service;

import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.model.Note;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.service.impl.NoteServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class NoteServiceTest {
    @Mock
    NoteMapper noteMapper;

    @Mock
    SnowFlakeUtils snowFlakeUtils;

    @Mock
    RelationService relationService;

    @InjectMocks
    NoteServiceImpl noteService;

    @Test
    void should_createNoteSuccessfully_withMinimalNote() {
        // Arrange
        NoteCreateRequest noteCreateRequest = new NoteCreateRequest();
        noteCreateRequest.setContent("Hello World!");
        noteCreateRequest.setProjectId("1");
        String tenantId = "test";

        // Act
        noteService.createNote(noteCreateRequest, tenantId);

        // Assert
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteMapper).insert(noteCaptor.capture());
        Note captureedNote = noteCaptor.getValue();
        assertThat(captureedNote.getContent()).isEqualTo(noteCreateRequest.getContent());
    }
}

package com.noteverso.core.dao;

import com.noteverso.core.model.entity.Note;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class NoteJsonIntegrationTest {

    @Autowired
    private NoteMapper noteMapper;

    @Test
    void testInsertAndRetrieveNoteWithJsonContent() {
        // Arrange
        Map<String, Object> jsonContent = Map.of(
            "type", "doc",
            "content", Map.of(
                "type", "paragraph",
                "content", Map.of("type", "text", "text", "Hello JSON World")
            )
        );

        Note note = Note.builder()
            .noteId("json-test-1")
            .contentJson(jsonContent)
            .projectId("test-project")
            .creator("test-user")
            .updater("test-user")
            .addedAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        // Act
        noteMapper.insertWithJsonb(note);
        Note retrieved = noteMapper.selectByNoteId("json-test-1", 0);

        // Assert
        assertThat(retrieved).isNotNull();
        
        // The JSON content should be returned as a String (which is correct for our use case)
        assertThat(retrieved.getContentJson()).isInstanceOf(String.class);
        String retrievedJsonString = (String) retrieved.getContentJson();
        assertThat(retrievedJsonString).contains("\"type\": \"doc\"");
        assertThat(retrievedJsonString).contains("\"text\": \"Hello JSON World\"");
    }
}

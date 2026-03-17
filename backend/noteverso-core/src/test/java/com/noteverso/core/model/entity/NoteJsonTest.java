package com.noteverso.core.model.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NoteJsonTest {

    @Test
    void testContentJsonSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        
        // Test JSON content
        Map<String, Object> jsonContent = Map.of(
            "type", "doc",
            "content", Map.of(
                "type", "paragraph",
                "content", Map.of("type", "text", "text", "Hello World")
            )
        );
        
        Note note = Note.builder()
            .contentJson(jsonContent)
            .build();
        
        assertThat(note.getContentJson()).isEqualTo(jsonContent);
    }
}

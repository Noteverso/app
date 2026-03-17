package com.noteverso.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class HtmlGeneratorServiceTest {

    private final HtmlGeneratorService htmlGeneratorService = new HtmlGeneratorService();

    @Test
    void should_generateHtml_fromSimpleJson() {
        // Arrange
        Map<String, Object> contentJson = Map.of(
            "type", "doc",
            "content", List.of(
                Map.of(
                    "type", "paragraph",
                    "content", List.of(
                        Map.of("type", "text", "text", "Hello World!")
                    )
                )
            )
        );

        // Act
        String html = htmlGeneratorService.generateHtml(contentJson);

        // Assert
        assertThat(html).isEqualTo("<p>Hello World!</p>");
    }

    @Test
    void should_generateHtml_fromComplexJson() {
        // Arrange
        Map<String, Object> contentJson = Map.of(
            "type", "doc",
            "content", List.of(
                Map.of(
                    "type", "heading",
                    "attrs", Map.of("level", 1),
                    "content", List.of(
                        Map.of("type", "text", "text", "Title")
                    )
                ),
                Map.of(
                    "type", "paragraph",
                    "content", List.of(
                        Map.of("type", "text", "text", "Normal text with "),
                        Map.of(
                            "type", "text", 
                            "text", "bold",
                            "marks", List.of(Map.of("type", "bold"))
                        ),
                        Map.of("type", "text", "text", " formatting.")
                    )
                )
            )
        );

        // Act
        String html = htmlGeneratorService.generateHtml(contentJson);

        // Assert
        assertThat(html).contains("<h1>Title</h1>");
        assertThat(html).contains("<p>Normal text with <strong>bold</strong> formatting.</p>");
    }

    @Test
    void should_returnEmpty_forNullContent() {
        // Act
        String html = htmlGeneratorService.generateHtml(null);

        // Assert
        assertThat(html).isEmpty();
    }

    @Test
    void should_returnEmpty_forEmptyContent() {
        // Act
        String html = htmlGeneratorService.generateHtml(Map.of());

        // Assert
        assertThat(html).isEmpty();
    }
}

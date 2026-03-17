package com.noteverso.core.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

/**
 * Service to generate HTML from ProseMirror JSON content
 */
@Service
public class HtmlGeneratorService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateHtml(Object contentJson) {
        if (contentJson == null) {
            return "";
        }

        try {
            JsonNode jsonNode = objectMapper.valueToTree(contentJson);
            return processNode(jsonNode);
        } catch (Exception e) {
            return "";
        }
    }

    private String processNode(JsonNode node) {
        if (node == null || !node.isObject()) {
            return "";
        }

        String type = node.path("type").asText();
        JsonNode content = node.path("content");

        switch (type) {
            case "doc":
                return processContent(content);
            case "paragraph":
                return "<p>" + processContent(content) + "</p>";
            case "heading":
                int level = node.path("attrs").path("level").asInt(1);
                return "<h" + level + ">" + processContent(content) + "</h" + level + ">";
            case "text":
                String text = node.path("text").asText();
                return processTextMarks(text, node.path("marks"));
            case "hardBreak":
                return "<br>";
            case "codeBlock":
                return "<pre><code>" + processContent(content) + "</code></pre>";
            case "blockquote":
                return "<blockquote>" + processContent(content) + "</blockquote>";
            case "bulletList":
                return "<ul>" + processContent(content) + "</ul>";
            case "orderedList":
                return "<ol>" + processContent(content) + "</ol>";
            case "listItem":
                return "<li>" + processContent(content) + "</li>";
            default:
                return processContent(content);
        }
    }

    private String processContent(JsonNode content) {
        if (!content.isArray()) {
            return "";
        }

        StringBuilder html = new StringBuilder();
        for (JsonNode child : content) {
            html.append(processNode(child));
        }
        return html.toString();
    }

    private String processTextMarks(String text, JsonNode marks) {
        if (!marks.isArray() || marks.size() == 0) {
            return escapeHtml(text);
        }

        String result = escapeHtml(text);
        for (JsonNode mark : marks) {
            String markType = mark.path("type").asText();
            switch (markType) {
                case "bold":
                    result = "<strong>" + result + "</strong>";
                    break;
                case "italic":
                    result = "<em>" + result + "</em>";
                    break;
                case "code":
                    result = "<code>" + result + "</code>";
                    break;
                case "strike":
                    result = "<s>" + result + "</s>";
                    break;
                case "link":
                    String href = mark.path("attrs").path("href").asText();
                    result = "<a href=\"" + escapeHtml(href) + "\">" + result + "</a>";
                    break;
            }
        }
        return result;
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}

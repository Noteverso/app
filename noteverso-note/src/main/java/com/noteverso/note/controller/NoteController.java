package com.noteverso.note.controller;

import com.noteverso.note.model.Note;
import com.noteverso.note.request.NoteCreateRequest;
import com.noteverso.note.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

@Tag(name = "Note", description = "Note management APIs")
@RestController
@RequestMapping("/api/note")
@AllArgsConstructor
public class NoteController {
    private NoteService noteService;

    @Operation(
        summary = "Create a Note",
        description = "Create a Note object by specifying its name and addedAt. The response is Note object with id, name and addedAt.",
        tags = { "Post" })
    @PostMapping("/create")
    public HashMap<String, Object> createNote(@Valid @RequestBody NoteCreateRequest request) {
        return noteService.createNote(request);
    }

    @Operation(
        summary = "Retrieve a Note by Id",
        description = "Get a Note object by specifying its id. The response is Tutorial object with id, title, description and published status.",
        tags = { "Get" })
    @ApiResponses({
        @ApiResponse(responseCode = "200", content = { @Content(schema = @Schema(implementation = Note.class), mediaType = "application/json")}),
        @ApiResponse(responseCode = "404", content = { @Content(schema = @Schema()) }),
        @ApiResponse(responseCode = "500", content = { @Content(schema = @Schema()) })})
    @GetMapping("/get/{id}")
    public HashMap<String, Object> getNote(@PathVariable("id") long id) {
        NoteCreateRequest request = new NoteCreateRequest();
        request.setName("example");
        request.setAddedAt(new Date());
        return noteService.createNote(request);
    }

    @GetMapping
    public String sayHello() {
        return "Hello world";
    }
}

package com.noteverso.note.controller;

import com.noteverso.note.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("/api/note")
@AllArgsConstructor
public class NoteController {
    private NoteService noteService;

    @PostMapping("/create")
    public HashMap<String, Object> createNote() {
        return noteService.createNote();
    }

    @GetMapping("/get")
    public HashMap<String, Object> getNote() {
        return noteService.createNote();
    }
}

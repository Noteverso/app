package com.noteverso.note.service.impl;

import com.noteverso.note.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class NoteServiceImpl implements NoteService {
    @Override
    public HashMap<String, Object> createNote() {
        HashMap<String, Object> note = new HashMap<>();
        note.put("name", "byodian");
        note.put("addedAt", "2023-06-20");
        return note;
    }

    @Override
    public String sayHello() {
        return "Hello World";
    }
}

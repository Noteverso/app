package com.noteverso.note.service.impl;

import com.noteverso.note.request.NoteCreateRequest;
import com.noteverso.note.service.NoteService;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class NoteServiceImpl implements NoteService {
    @Override
    public HashMap<String, Object> createNote(NoteCreateRequest request) {
        HashMap<String, Object> note = new HashMap<>();
        note.put("name", request.getName());
        note.put("addedAt", request.getAddedAt().toString());
        return note;
    }

    @Override
    public String sayHello() {
        return "Hello World";
    }
}

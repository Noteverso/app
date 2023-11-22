package com.noteverso.note.service;

import com.noteverso.note.request.NoteCreateRequest;
import com.noteverso.note.request.NoteUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface NoteService {
    void createNote(NoteCreateRequest request);

    void updateNote(String id, NoteUpdateRequest request);
}

package com.noteverso.note.service;

import com.noteverso.note.request.NoteCreateRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface NoteService {
    void createNote(NoteCreateRequest request);

    String sayHello();
}

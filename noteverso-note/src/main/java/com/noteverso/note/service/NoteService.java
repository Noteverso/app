package com.noteverso.note.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public interface NoteService {
    HashMap<String, Object> createNote();

    String sayHello();
}

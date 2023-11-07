package com.noteverso.note.service.impl;

import static com.noteverso.common.constant.NumberEnum.NUM_31;

import com.noteverso.attachment.request.AttachmentRequest;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.note.dao.NoteMapper;
import com.noteverso.note.model.Note;
import com.noteverso.note.request.NoteCreateRequest;
import com.noteverso.note.service.NoteService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class NoteServiceImpl implements NoteService {
    private final NoteMapper noteMapper;
    private final SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            1L, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Override
    public void createNote(NoteCreateRequest request) {
        List<String> labels = request.getLabels();
        List<String> likedNotes = request.getLinkedNotes();
        List<AttachmentRequest> files = request.getFiles();

        Note note = Note.builder()
                .noteId(String.valueOf(snowFlakeUtils.nextId()))
                .content(request.getContent())
                .projectId(request.getProjectId())
                .creator(1L)
                .updater(1L)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        noteMapper.insert(note);
    }

    @Override
    public String sayHello() {
        return "Hello World";
    }
}

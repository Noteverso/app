package com.noteverso.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.model.Note;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NoteUpdateRequest;
import com.noteverso.core.service.RelationService;
import lombok.AllArgsConstructor;
import com.noteverso.core.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.noteverso.common.constant.NumConstants.*;

@Service
@AllArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {
    private final NoteMapper noteMapper;
    private final RelationService relationService;

    private final SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            NOTE_DATACENTER_ID, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNote(NoteCreateRequest request, String tenantId) {
        String projectId = request.getProjectId();
        String noteId = String.valueOf(snowFlakeUtils.nextId());

        // Create a note
        Note note = constructNote(noteId, projectId, request.getContent(), tenantId);
        noteMapper.insert(note);

        // create the relation with labels
        relationService.insertNoteLabelRelation(request.getLabels(), noteId, tenantId);

        // create the relation with the other notes
        relationService.insertNoteRelation(request.getLinkedNotes(), noteId, tenantId);

        //  create the relation with attachments
        relationService.insertNoteAttachmentRelation(request.getFiles(), noteId, tenantId);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateNote(String noteId, String tenantId, NoteUpdateRequest request) {
        LambdaUpdateWrapper<Note> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Note::getNoteId, noteId);
        wrapper.set(Note::getContent, request.getContent());
        wrapper.set(Note::getUpdatedAt, Instant.now());

        if (null != request.getProjectId()) {
            wrapper.set(Note::getProjectId, request.getProjectId());
        }

        noteMapper.update(null, wrapper);

        // update the relation with labels

        relationService.updateNoteLabelRelation(request.getLabels(), noteId, tenantId);
        // update the relation with the other notes
        relationService.updateNoteRelation(request.getLinkedNotes(), noteId, tenantId);

        // update the relation with the attachments
        relationService.updateNoteAttachment(request.getFiles(), noteId, tenantId);
    }

    public Note constructNote(String noteId, String projectId, String content, String tenantId) {
        return Note.builder()
                .noteId(noteId)
                .content(content)
                .projectId(projectId)
                .creator(tenantId)
                .updater(tenantId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Override
    public void toggleVisibility(String id, Boolean toggle) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);

        if (toggle) {
            noteWrapper.set(Note::getIsDeleted, NUM_O);
        } else {
            noteWrapper.set(Note::getIsDeleted, NUM_1);
        }

        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);
    }

    @Override
    public void toggleArchive(String id, Boolean toggle) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);
        if (toggle) {
            noteWrapper.set(Note::getIsArchived, NUM_1);
        } else {
            noteWrapper.set(Note::getIsArchived, NUM_O);
        }
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);

    };

    @Override
    public void toggleFavorite(String id, Boolean toggle) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);
        if (toggle) {
            noteWrapper.set(Note::getIsFavorite, NUM_1);
        } else {
            noteWrapper.set(Note::getIsFavorite, NUM_O);
        }
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);
    };

    @Override
    public void togglePin(String id, Boolean toggle) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);
        if (toggle) {
            noteWrapper.set(Note::getIsPin, NUM_1);
        } else {
            noteWrapper.set(Note::getIsPin, NUM_O);
        }
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);
    }

    @Override
    public void moveNote(String id, String projectId) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);
        // TODO
        // check project
        noteWrapper.set(Note::getProjectId, projectId);
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);
    }
}

package com.noteverso.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.common.exceptions.BaseException;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.AttachmentRelationMapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.NoteRelationMapper;
import com.noteverso.core.model.AttachmentRelation;
import com.noteverso.core.model.Note;
import com.noteverso.core.model.NoteLabelRelation;
import com.noteverso.core.model.NoteRelation;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NoteUpdateRequest;
import lombok.AllArgsConstructor;
import com.noteverso.core.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.noteverso.common.constant.NumConstants.*;

@Service
@AllArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {
    private final NoteMapper noteMapper;
    private final NoteLabelRelationMapper noteLabelRelationMapper;
    private final NoteRelationMapper noteRelationMapper;
    private final AttachmentRelationMapper attachmentRelationMapper;

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
        insertNoteLabelRelation(request.getLabels(), noteId, tenantId);

        // create the relation with the other notes
        insertNoteRelation(request.getLinkedNotes(), noteId, tenantId);

        //  create the relation with attachments
        insertNoteAttachmentRelation(request.getFiles(), noteId, tenantId);
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
        updateNoteLabelRelation(request.getLabels(), noteId, tenantId);

        // update the relation with the other notes
        updateNoteRelation(request.getLinkedNotes(), noteId, tenantId);

        // update the relation with the attachments
        updateNoteAttachment(request.getFiles(), noteId, tenantId);
    }

    private void insertNoteLabelRelation(List<String> labels, String noteId, String tenantId) {
        if (labels == null || labels.isEmpty()) {
            return;
        }

        List<NoteLabelRelation> noteLabelRelations = new ArrayList<>();
        for(String label : labels) {
            NoteLabelRelation noteLabelRelation = constructNoteLabelRelation(label, noteId, tenantId);
            noteLabelRelations.add(noteLabelRelation);
        }

        noteLabelRelationMapper.batchInsert(noteLabelRelations);
    }

    private void insertNoteAttachmentRelation(List<String> attachments, String noteId, String tenantId) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        List<AttachmentRelation> attachmentRelations = new ArrayList<>();
        for(String attachmentId : attachments) {
            AttachmentRelation attachmentRelation = constructAttachmentRelation(attachmentId, noteId, tenantId);
            attachmentRelations.add(attachmentRelation);
        }

        attachmentRelationMapper.batchInsert(attachmentRelations);
    }

    private void insertNoteRelation(List<String> linkedNotes, String noteId, String tenantId) {
        if (linkedNotes == null || linkedNotes.isEmpty()) {
            return;
        }

        List<NoteRelation> noteRelations = new ArrayList<>();
        for (String linkedNote : linkedNotes) {
            NoteRelation noteRelation = constructNoteRelation(noteId, linkedNote, tenantId);
            noteRelations.add(noteRelation);
        }
        noteRelationMapper.batchInsert(noteRelations);
    }

    private void updateNoteRelation(List<String> linkedNotes, String noteId, String tenantId) {
        if (linkedNotes == null || linkedNotes.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<NoteRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteRelation::getNoteId, noteId);
        noteRelationMapper.delete(updateWrapper);

        insertNoteRelation(linkedNotes, noteId, tenantId);
    }

    private void updateNoteLabelRelation(List<String> labels, String noteId, String tenantId) {
        if (labels == null || labels.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<NoteLabelRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteLabelRelation::getNoteId, noteId);
        noteLabelRelationMapper.delete(updateWrapper);

        insertNoteLabelRelation(labels, noteId, tenantId);
    }

    private void updateNoteAttachment(List<String> attachmentIds, String noteId, String tenantId) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<AttachmentRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AttachmentRelation::getObjectId, noteId);
        attachmentRelationMapper.delete(updateWrapper);

        insertNoteAttachmentRelation(attachmentIds, noteId, tenantId);
    }

    private Note constructNote(String noteId, String projectId, String content, String tenantId) {
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
    private NoteLabelRelation constructNoteLabelRelation(String labelId, String noteId, String tenantId) {
       return NoteLabelRelation
               .builder()
               .noteId(noteId)
               .labelId(labelId)
               .updater(tenantId)
               .creator(tenantId)
               .addedAt(Instant.now())
               .updatedAt(Instant.now())
               .build();
    }

    private NoteRelation constructNoteRelation(String noteId, String linkedNoteId, String tenantId) {
        return NoteRelation
                .builder()
                .noteId(noteId)
                .linkedNoteId(linkedNoteId)
                .viewStyle(0)
                .creator(tenantId)
                .updater(tenantId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private AttachmentRelation constructAttachmentRelation(String attachmentId, String noteId, String tenantId) {
        return AttachmentRelation
                .builder()
                .attachmentId(attachmentId)
                .objectId(noteId)
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

    public boolean containsSwearWords(String comment) {
        if (comment.contains("shit")) {
            throw new BaseException("Comments contains unacceptable language");
        }
        return false;
    }
}

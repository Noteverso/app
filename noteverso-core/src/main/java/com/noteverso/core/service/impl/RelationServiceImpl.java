package com.noteverso.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.core.dao.AttachmentRelationMapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.dao.NoteRelationMapper;
import com.noteverso.core.model.AttachmentRelation;
import com.noteverso.core.model.NoteLabelRelation;
import com.noteverso.core.model.NoteRelation;
import com.noteverso.core.service.RelationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class RelationServiceImpl implements RelationService {
    private final NoteLabelRelationMapper noteLabelRelationMapper;
    private final NoteRelationMapper noteRelationMapper;
    private final AttachmentRelationMapper attachmentRelationMapper;

    @Override
    public void insertNoteLabelRelation(List<String> labels, String noteId, String tenantId) {
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

    @Override
    public void insertNoteAttachmentRelation(List<String> attachments, String noteId, String tenantId) {
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

    @Override
    public void insertNoteRelation(List<String> linkedNotes, String noteId, String tenantId) {
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

    @Override
    public void updateNoteRelation(List<String> linkedNotes, String noteId, String tenantId) {
        if (linkedNotes == null || linkedNotes.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<NoteRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteRelation::getNoteId, noteId);
        noteRelationMapper.delete(updateWrapper);

        insertNoteRelation(linkedNotes, noteId, tenantId);
    }

    @Override
    public void updateNoteLabelRelation(List<String> labels, String noteId, String tenantId) {
        if (labels == null || labels.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<NoteLabelRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteLabelRelation::getNoteId, noteId);
        noteLabelRelationMapper.delete(updateWrapper);

        insertNoteLabelRelation(labels, noteId, tenantId);
    }

    @Override
    public void updateNoteAttachment(List<String> attachmentIds, String noteId, String tenantId) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<AttachmentRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AttachmentRelation::getObjectId, noteId);
        attachmentRelationMapper.delete(updateWrapper);

        insertNoteAttachmentRelation(attachmentIds, noteId, tenantId);
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
}

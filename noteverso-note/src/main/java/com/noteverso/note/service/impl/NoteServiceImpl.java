package com.noteverso.note.service.impl;

import static com.noteverso.common.constant.NumConstants.NUM_31;

import com.noteverso.common.context.TenantContext;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dto.AttachmentDTO;
import com.noteverso.core.request.AttachmentRequest;
import com.noteverso.core.service.IAttachmentService;
import com.noteverso.note.dao.NoteLabelRelationMapper;
import com.noteverso.note.dao.NoteMapper;
import com.noteverso.note.dao.NoteProjectRelationMapper;
import com.noteverso.note.dao.NoteRelationMapper;
import com.noteverso.note.model.Note;
import com.noteverso.note.model.NoteLabelRelation;
import com.noteverso.note.model.NoteProjectRelation;
import com.noteverso.note.model.NoteRelation;
import com.noteverso.note.request.NoteCreateRequest;
import com.noteverso.note.service.NoteService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {
    private final NoteMapper noteMapper;
    private final NoteLabelRelationMapper noteLabelRelationMapper;
    private final NoteProjectRelationMapper noteProjectRelationMapper;
    private final NoteRelationMapper noteRelationMapper;
    private final IAttachmentService attachmentService;

    private final SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            1L, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNote(NoteCreateRequest request) {
        String tenantId = TenantContext.getTenantId();
        String projectId = request.getProjectId();
        String noteId = String.valueOf(snowFlakeUtils.nextId());

        // Create a note
        Note note = constructNote(noteId, projectId, request.getContent(), tenantId);
        noteMapper.insert(note);

        // create the relation with a project
        NoteProjectRelation noteProjectRelation = constructNoteProjectRelation(noteId, projectId, tenantId);
        noteProjectRelationMapper.insert(noteProjectRelation);

        // create the relation with labels
        List<String> labels = request.getLabels();
        if (labels != null && !labels.isEmpty()) {
            List<NoteLabelRelation> noteLabelRelations = new ArrayList<>();
            for(String label : labels) {
                NoteLabelRelation noteLabelRelation = constructNoteLabelRelation(label, noteId, tenantId);
                noteLabelRelations.add(noteLabelRelation);
            }
           noteLabelRelationMapper.batchInsert(noteLabelRelations);
        }

        // create the relation with the other notes
        List<String> likedNotes = request.getLinkedNotes();
        if (likedNotes != null && !likedNotes.isEmpty()) {
            List<NoteRelation> noteRelations = new ArrayList<>();
            for (String linkedNote : likedNotes) {
                NoteRelation noteRelation = constructNoteRelation(noteId, linkedNote, tenantId);
                noteRelations.add(noteRelation);
            }
            noteRelationMapper.batchInsert(noteRelations);
        }

        //  create the relation with attachments
        List<AttachmentRequest> files = request.getFiles();
        if (files != null && !files.isEmpty()) {
            List<AttachmentDTO> attachmentDTOS = new ArrayList<>();
            for(AttachmentRequest file : files) {
                AttachmentDTO attachmentDTO = new AttachmentDTO();
                attachmentDTO.setNoteId(noteId);
                attachmentDTO.setSize(file.getSize());
                attachmentDTO.setType(file.getType());
                attachmentDTO.setName(file.getName());
                attachmentDTO.setUrl(file.getUrl());
                attachmentDTO.setResourceType(file.getResourceType());
                attachmentDTOS.add(attachmentDTO);
            }
            attachmentService.createMultipleAttachments(attachmentDTOS);
        }
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

    private NoteProjectRelation constructNoteProjectRelation(String noteId, String projectId, String tenantId) {
        return NoteProjectRelation
                .builder()
                .noteId(noteId)
                .projectId(projectId)
                .creator(tenantId)
                .updater(tenantId)
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
}

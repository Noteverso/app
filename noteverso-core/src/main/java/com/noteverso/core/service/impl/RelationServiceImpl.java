package com.noteverso.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.core.dao.AttachmentRelationMapper;
import com.noteverso.core.dao.NoteLabelRelationMapper;
import com.noteverso.core.dao.NoteRelationMapper;
import com.noteverso.core.dto.AttachmentCount;
import com.noteverso.core.dto.ReferencedNoteCount;
import com.noteverso.core.dto.ReferencingNoteCount;
import com.noteverso.core.model.AttachmentRelation;
import com.noteverso.core.model.NoteLabelRelation;
import com.noteverso.core.model.NoteRelation;
import com.noteverso.core.service.RelationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class RelationServiceImpl implements RelationService {
    private final NoteLabelRelationMapper noteLabelRelationMapper;
    private final NoteRelationMapper noteRelationMapper;
    private final AttachmentRelationMapper attachmentRelationMapper;

    @Override
    public void insertNoteLabelRelation(List<String> labels, String noteId, String userId) {
        if (labels == null || labels.isEmpty()) {
            return;
        }

        List<NoteLabelRelation> noteLabelRelations = new ArrayList<>();
        for(String label : labels) {
            NoteLabelRelation noteLabelRelation = constructNoteLabelRelation(label, noteId, userId);
            noteLabelRelations.add(noteLabelRelation);
        }

        noteLabelRelationMapper.batchInsert(noteLabelRelations);
    }

    @Override
    public void insertNoteAttachmentRelation(List<String> attachments, String noteId, String userId) {
        if (attachments == null || attachments.isEmpty()) {
            return;
        }

        List<AttachmentRelation> attachmentRelations = new ArrayList<>();
        for(String attachmentId : attachments) {
            AttachmentRelation attachmentRelation = constructAttachmentRelation(attachmentId, noteId, userId);
            attachmentRelations.add(attachmentRelation);
        }

        attachmentRelationMapper.batchInsert(attachmentRelations);
    }

    @Override
    public void insertNoteRelation(List<String> linkedNotes, String noteId, String userId) {
        if (linkedNotes == null || linkedNotes.isEmpty()) {
            return;
        }

        List<NoteRelation> noteRelations = new ArrayList<>();
        for (String linkedNote : linkedNotes) {
            NoteRelation noteRelation = constructNoteRelation(noteId, linkedNote, userId);
            noteRelations.add(noteRelation);
        }
        noteRelationMapper.batchInsert(noteRelations);
    }

    private NoteLabelRelation constructNoteLabelRelation(String labelId, String noteId, String userId) {
        return NoteLabelRelation
                .builder()
                .noteId(noteId)
                .labelId(labelId)
                .updater(userId)
                .creator(userId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private NoteRelation constructNoteRelation(String noteId, String linkedNoteId, String userId) {
        return NoteRelation
                .builder()
                .noteId(noteId)
                .linkedNoteId(linkedNoteId)
                .viewStyle(0)
                .creator(userId)
                .updater(userId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    private AttachmentRelation constructAttachmentRelation(String attachmentId, String noteId, String userId) {
        return AttachmentRelation
                .builder()
                .attachmentId(attachmentId)
                .objectId(noteId)
                .creator(userId)
                .updater(userId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Override
    public void updateNoteRelation(List<String> linkedNotes, String noteId, String userId) {
        if (linkedNotes == null || linkedNotes.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<NoteRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteRelation::getNoteId, noteId);
        noteRelationMapper.delete(updateWrapper);

        insertNoteRelation(linkedNotes, noteId, userId);
    }

    @Override
    public void updateNoteLabelRelation(List<String> labels, String noteId, String userId) {
        if (labels == null || labels.isEmpty()) {
            return;
        }
        LambdaUpdateWrapper<NoteLabelRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteLabelRelation::getNoteId, noteId);
        noteLabelRelationMapper.delete(updateWrapper);

        insertNoteLabelRelation(labels, noteId, userId);
    }

    @Override
    public void updateNoteAttachment(List<String> attachmentIds, String noteId, String userId) {
        if (attachmentIds == null || attachmentIds.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<AttachmentRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AttachmentRelation::getObjectId, noteId);
        attachmentRelationMapper.delete(updateWrapper);

        insertNoteAttachmentRelation(attachmentIds, noteId, userId);
    }


    @Override
    public void deleteNoteRelation(String noteId, String userId) {
        LambdaUpdateWrapper<NoteRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(NoteRelation::getNoteId, noteId)
                .or()
                .eq(NoteRelation::getLinkedNoteId, noteId);
        updateWrapper.eq(NoteRelation::getCreator, userId);
        noteRelationMapper.delete(updateWrapper);
    }

    @Override
    public void deleteNoteLabelRelation(String noteId, String userId) {
        LambdaUpdateWrapper<NoteLabelRelation>  updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(NoteLabelRelation::getNoteId, noteId);
        updateWrapper.eq(NoteLabelRelation::getCreator, userId);
        noteLabelRelationMapper.delete(updateWrapper);
    }

    @Override
    public void deleteNoteAttachmentRelation(String noteId, String userId) {
        LambdaUpdateWrapper<AttachmentRelation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AttachmentRelation::getObjectId, noteId);
        updateWrapper.eq(AttachmentRelation::getCreator, userId);
        attachmentRelationMapper.delete(updateWrapper);
    }

    @Override
    public List<String> getReferencingNotes(String referencingNoteId, String userId) {
        LambdaQueryWrapper<NoteRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoteRelation::getNoteId, referencingNoteId);
        queryWrapper.eq(NoteRelation::getCreator, userId);
        List<NoteRelation> noteRelations = noteRelationMapper.selectList(queryWrapper);
        List<String> referencedNotes = new ArrayList<>();

        if (noteRelations != null && !noteRelations.isEmpty()) {
            for (NoteRelation noteRelation : noteRelations) {
                referencedNotes.add(noteRelation.getLinkedNoteId());
            }
        }
        return referencedNotes;
    }

    @Override
    public List<String> getReferencedNotes(String referencedNoteId, String userId) {
        LambdaQueryWrapper<NoteRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoteRelation::getLinkedNoteId, referencedNoteId);
        queryWrapper.eq(NoteRelation::getCreator, userId);
        List<NoteRelation> noteRelations = noteRelationMapper.selectList(queryWrapper);
        List<String> referringNotes = new ArrayList<>();

        if (noteRelations != null && !noteRelations.isEmpty()) {
            for (NoteRelation noteRelation : noteRelations) {
                referringNotes.add(noteRelation.getNoteId());
            }
        }
        return referringNotes;
    }

    @Override
    public List<String> getAttachmentsByNoteId(String noteId, String userId) {
        LambdaQueryWrapper<AttachmentRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AttachmentRelation::getObjectId, noteId);
        queryWrapper.eq(AttachmentRelation::getCreator, userId);
        List<AttachmentRelation> attachmentRelations = attachmentRelationMapper.selectList(queryWrapper);
        List<String> attachmentIds = new ArrayList<>();

        if (attachmentRelations != null && !attachmentRelations.isEmpty()) {
            for (AttachmentRelation attachmentRelation: attachmentRelations) {
                attachmentIds.add(attachmentRelation.getAttachmentId());
            }
        }
        return attachmentIds;
    }

    @Override
    public List<String> getLabelsByNoteId(String noteId, String userId) {
        LambdaQueryWrapper<NoteLabelRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoteLabelRelation::getNoteId, noteId);
        queryWrapper.eq(NoteLabelRelation::getCreator, userId);
        List<NoteLabelRelation> noteLabelRelations = noteLabelRelationMapper.selectList(queryWrapper);
        List<String> labelIds = new ArrayList<>();

        if (noteLabelRelations != null && !noteLabelRelations.isEmpty()) {
            for (NoteLabelRelation noteLabelRelation: noteLabelRelations) {
                labelIds.add(noteLabelRelation.getLabelId());
            }
        }
        return labelIds;
    }

    @Override
    public HashMap<String, Long> getAttachmentCountByObjectIds(List<String> objectIds, String userId) {
        HashMap<String, Long> attachmentCountMap = new HashMap<>();
        List<AttachmentCount> attachmentCountList = attachmentRelationMapper.getAttachmentCountByObjectIds(objectIds, userId);
        if (attachmentCountList != null && !attachmentCountList.isEmpty()) {
            for (AttachmentCount attachmentCount : attachmentCountList) {
                attachmentCountMap.put(attachmentCount.getObjectId(), attachmentCount.getAttachmentCount());
            }
        }
        return attachmentCountMap;
    }

    @Override
    public HashMap<String, Long> getReferencedCountByReferencedNoteIds(List<String> referencedNoteIds, String userId) {
        HashMap<String, Long> referencedNoteCountMap = new HashMap<>();
        List<ReferencedNoteCount> referencedCountList = noteRelationMapper.getReferencedNoteCountByReferencedIds(referencedNoteIds, userId);
        if (referencedCountList != null && !referencedCountList.isEmpty()) {
            for (ReferencedNoteCount referencedCount : referencedCountList) {
                referencedNoteCountMap.put(referencedCount.getReferencedNoteId(), referencedCount.getReferencedNoteCount());
            }
        }
        return referencedNoteCountMap;
    }

    @Override
    public HashMap<String, Long> getReferencingCountByReferencingNoteIds(List<String> referencingNoteIds, String userId) {
        HashMap<String, Long> referencingNoteCountMap = new HashMap<>();
        List<ReferencingNoteCount> referencingNoteCountList = noteRelationMapper.getReferencingNoteCountByReferencingIds(referencingNoteIds, userId);
        if (referencingNoteCountList != null && !referencingNoteCountList.isEmpty()) {
            for (ReferencingNoteCount referencedNoteCount : referencingNoteCountList) {
                referencingNoteCountMap.put(referencedNoteCount.getReferencingNoteId(), referencedNoteCount.getReferencingNoteCount());
            }
        }
        return referencingNoteCountMap;
    }
}

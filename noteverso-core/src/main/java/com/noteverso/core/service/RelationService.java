package com.noteverso.core.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 各种处理笔记与其他实体关系的服务
 */

public interface RelationService {
    void insertNoteLabelRelation(List<String> labels, String noteId, String userId);

    void insertNoteAttachmentRelation(List<String> attachments, String noteId, String userId);

    void insertNoteRelation(List<String> linkedNotes, String noteId, String userId);

    void updateNoteRelation(List<String> linkedNotes, String noteId, String userId);

    void updateNoteLabelRelation(List<String> labels, String noteId, String userId);

    void updateNoteAttachment(List<String> attachmentIds, String noteId, String userId);

    void deleteNoteRelation(String noteId, String userId);

    void deleteNoteLabelRelation(String noteId, String userId);

    void deleteNoteAttachmentRelation(String noteId, String userId);
}

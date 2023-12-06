package com.noteverso.core.service;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 各种处理笔记与其他实体关系的服务
 */

public interface RelationService {
    void insertNoteLabelRelation(List<String> labels, String noteId, String tenantId);

    void insertNoteAttachmentRelation(List<String> attachments, String noteId, String tenantId);

    void insertNoteRelation(List<String> linkedNotes, String noteId, String tenantId);

    void updateNoteRelation(List<String> linkedNotes, String noteId, String tenantId);

    void updateNoteLabelRelation(List<String> labels, String noteId, String tenantId);

    void updateNoteAttachment(List<String> attachmentIds, String noteId, String tenantId);
}

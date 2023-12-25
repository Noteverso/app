package com.noteverso.core.service;

import java.util.HashMap;
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

    /**
     * 获取特定笔记引用的所有笔记 ID 列表。
     * @param referencingNoteId 引用（或指向）其他笔记的笔记 ID
     * @param userId
     * @return
     */
    List<String> getReferencedNotesFromNote(String referencingNoteId, String userId);

    /**
     * 获取指向特定笔记的所有笔记 ID 列表。
     * @param referencedNoteId 被引用的笔记 ID
     * @param userId
     * @return
     */
    List<String> getReferringNotesToNote(String referencedNoteId, String userId);

    List<String> getAttachmentsByNoteId(String noteId, String userId);

    List<String> getLabelsByNoteId(String noteId, String userId);

    /**
     * 获取指定对象关联的附件数量
     * @param objectIds 对象ids
     * @param userId creator
     * @return {objectId, attachmentCount}
     */
    HashMap<String, Long> getAttachmentCountByObjectIds(List<String> objectIds, String userId);

    HashMap<String, Long> getReferencedCountByReferencedNoteIds(List<String> referencedNoteIds, String userId);

    HashMap<String, Long> getReferencingCountByReferencingNoteIds(List<String> referencingNoteIds, String userId);
}

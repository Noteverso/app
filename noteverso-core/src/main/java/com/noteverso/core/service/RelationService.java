package com.noteverso.core.service;

import com.noteverso.core.model.NoteLabelRelation;

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
     * 获取当前笔记引用(或指向)的笔记ID列表。
     * @param referencingNoteId 发起引用的笔记ID
     * @param userId
     * @return
     */
    List<String> getReferencingNotes(String referencingNoteId, String userId);

    /**
     * 获取引用当前笔记的笔记ID列表
     * @param referencedNoteId 发起引用的笔记 ID
     * @param userId
     * @return
     */
    List<String> getReferencedNotes(String referencedNoteId, String userId);

    List<String> getAttachmentsByNoteId(String noteId, String userId);

    List<String> getLabelsByNoteId(String noteId, String userId);

    HashMap<String, List<String>> getLabelsByNoteIds(List<String> noteIds, String userId);

    /**
     * 获取指定对象关联的附件数量
     * @param objectIds 对象ids
     * @param userId creator
     * @return {objectId, attachmentCount}
     */
    HashMap<String, Long> getAttachmentCountByObjectIds(List<String> objectIds, String userId);

    HashMap<String, Long> getReferencedCountByReferencedNoteIds(List<String> referencedNoteIds, String userId);

    HashMap<String, Long> getReferencingCountByReferencingNoteIds(List<String> referencingNoteIds, String userId);

    HashMap<String, Long> getNoteCountByLabels(List<String> labelIds, String userId);

    List<NoteLabelRelation> getNoteLabelRelations(String labelId, String userId);
}

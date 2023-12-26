package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.Note;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
    Note selectByNoteId(String noteId, Integer isDeleted);
    List<Note> batchSelect(List<String> noteIds, String userId, Integer isDeleted);

    void updateNoteIsArchivedByProject(String projectId, String userId, Integer isArchived);

    void updateNotesIsDeletedByProject(String projectId, String userId);
}

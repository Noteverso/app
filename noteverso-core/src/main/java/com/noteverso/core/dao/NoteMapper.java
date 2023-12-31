package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.dto.NoteCountForProject;
import com.noteverso.core.dto.ProjectViewOption;
import com.noteverso.core.model.Note;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
    Note selectByNoteId(String noteId, Integer isDeleted);
    List<Note> batchSelectByNoteIds(List<String> noteIds, String userId, Integer isDeleted);

    List<NoteCountForProject> getNoteCountByProjects(List<ProjectViewOption> projectViewOptions, String userId);

    void updateNoteIsArchivedByProject(String projectId, String userId, Integer isArchived);

    void updateNotesIsDeletedByProject(String projectId, String userId);
}

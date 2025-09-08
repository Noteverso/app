package com.noteverso.core.service;

import com.noteverso.core.model.dto.NoteItem;
import com.noteverso.core.model.dto.ProjectDTO;
import com.noteverso.core.model.dto.ProjectItem;
import com.noteverso.core.model.dto.SelectItem;
import com.noteverso.core.model.entity.Project;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.request.*;

import java.util.List;

public interface ProjectService {
    String createProject(ProjectCreateRequest request, String userId);

    void updateProject(String projectId, ProjectUpdateRequest request, String userId);

    void archiveProject(String projectId, String userId);

    void unarchiveProject(String projectId, String userId);

    void deleteProject(String projectId, String userId);

    void favoriteProject(String projectId, String userId);

    void unFavoriteProject(String projectId, String userId);

    Project constructProject(ProjectDTO projectDTO);

    Project constructInboxProject(String userId);

    List<ProjectItem> getProjectList(String userId, ProjectListRequest request);

    List<SelectItem> getProjectSelectItems(ProjectRequest request, String userId);

    /**
     * modify the note view option and return the page of notes
     * @param request
     * @param userId
     * @return
     */
    PageResult<NoteItem> getNotePageByProject(String projectId, NotePageRequest request, String userId);

    PageResult<NoteItem> getInboxNotePage(NotePageRequest request, String userId);
}

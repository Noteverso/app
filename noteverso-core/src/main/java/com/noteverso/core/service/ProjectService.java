package com.noteverso.core.service;

import com.noteverso.core.dto.ProjectDTO;
import com.noteverso.core.model.Project;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.request.ProjectUpdateRequest;

public interface ProjectService {
    void createProject(ProjectCreateRequest request, String userId);

    void updateProject(String projectId, ProjectUpdateRequest request, String userId);

    void archiveProject(String projectId, String userId);

    void unarchiveProject(String projectId, String userId);

    void deleteProject(String projectId, String userId);

    void favoriteProject(String projectId, String userId);

    void unFavoriteProject(String projectId, String userId);

    Project constructProject(ProjectDTO projectDTO);

    Project constructInboxProject(String userId);
}

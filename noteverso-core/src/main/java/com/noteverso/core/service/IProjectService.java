package com.noteverso.core.service;

import com.noteverso.core.dto.ProjectDTO;
import com.noteverso.core.model.Project;
import com.noteverso.core.request.ProjectCreateRequest;

public interface IProjectService {
    void createProject(ProjectCreateRequest request);

    Project constructProject(ProjectDTO projectDTO);

    Project constructInboxProject(String userId);
}

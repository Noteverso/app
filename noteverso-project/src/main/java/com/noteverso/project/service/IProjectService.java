package com.noteverso.project.service;

import com.noteverso.project.dto.ProjectDTO;
import com.noteverso.project.model.Project;
import com.noteverso.project.request.ProjectCreateRequest;

public interface IProjectService {
    void createProject(ProjectCreateRequest request);

    Project constructProject(ProjectDTO projectDTO);

    Project constructInboxProject(String userId);
}

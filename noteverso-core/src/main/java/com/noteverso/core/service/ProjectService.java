package com.noteverso.core.service;

import com.noteverso.core.dto.ProjectDTO;
import com.noteverso.core.model.Project;
import com.noteverso.core.request.ProjectCreateRequest;

public interface ProjectService {
    void createProject(ProjectCreateRequest request, String tenantId);

    Project constructProject(ProjectDTO projectDTO);

    Project constructInboxProject(String userId);
}

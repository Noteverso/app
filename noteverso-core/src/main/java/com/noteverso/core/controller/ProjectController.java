package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.manager.impl.AuthManagerImpl;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.request.ProjectUpdateRequest;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/projects")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;
    private static final AuthManager authManager = new AuthManagerImpl();


    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<Void> createProject(Authentication authentication, @Valid @RequestBody ProjectCreateRequest request) {
        projectService.createProject(request, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @PostMapping("/{projectId}")
    public ApiResult<Void> updateProject(Authentication authentication ,@PathVariable("projectId") String projectId, @Valid @RequestBody ProjectUpdateRequest request) {
        projectService.updateProject(projectId, request, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @PostMapping("/{projectId}/archive")
    public ApiResult<Void> archiveProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.archiveProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @PostMapping("/{projectId}/unarchive")
    public ApiResult<Void> unarchiveProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.unarchiveProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @DeleteMapping("/{projectId}")
    public ApiResult<Void> deleteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.deleteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @PostMapping("/{projectId}/favorite")
    public ApiResult<Void> favoriteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.favoriteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @PostMapping("/{projectId}/unfavorite")
    public ApiResult<Void> unFavoriteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.unFavoriteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }
}

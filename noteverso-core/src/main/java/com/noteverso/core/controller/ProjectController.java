package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.core.dto.ProjectItem;
import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.manager.impl.AuthManagerImpl;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.request.ProjectUpdateRequest;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Project", description = "Project management APIs")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/projects")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;
    private static final AuthManager authManager = new AuthManagerImpl();


    @Operation(summary = "Create a Project", description = "Create a Project", tags = { "POST" })
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<String> createProject(Authentication authentication, @Valid @RequestBody ProjectCreateRequest request) {
        String projectId = projectService.createProject(request, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(projectId);
    }

    @Operation(summary = "Update a Project", description = "Update a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}")
    public ApiResult<Void> updateProject(Authentication authentication ,@PathVariable("projectId") String projectId, @Valid @RequestBody ProjectUpdateRequest request) {
        projectService.updateProject(projectId, request, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Archive a Project", description = "Archive a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}/archive")
    public ApiResult<Void> archiveProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.archiveProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Unarchive a Project", description = "Unarchive a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}/unarchive")
    public ApiResult<Void> unarchiveProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.unarchiveProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Delete a Project", description = "Delete a Project", tags = { "DELETE" })
    @DeleteMapping("/{projectId}")
    public ApiResult<Void> deleteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.deleteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Favorite a Project", description = "Favorite a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}/favorite")
    public ApiResult<Void> favoriteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.favoriteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "UnFavorite a Project", description = "UnFavorite a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}/unfavorite")
    public ApiResult<Void> unFavoriteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.unFavoriteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return ApiResult.success(null);
    }

    @Operation(summary = "Get Project List", description = "Get Project List", tags = { "GET" })
    @GetMapping("")
    public ApiResult<List<ProjectItem>> getProjectList(Authentication authentication) {
        UserDetailsImpl userDetails = authManager.getPrincipal(authentication) ;
        return ApiResult.success(projectService.getProjectList(userDetails.getUserId()));
    }
}

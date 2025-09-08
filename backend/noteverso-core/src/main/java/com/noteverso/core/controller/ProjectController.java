package com.noteverso.core.controller;

import com.noteverso.core.model.dto.NoteItem;
import com.noteverso.core.model.dto.ProjectItem;
import com.noteverso.core.model.dto.SelectItem;
import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.manager.impl.AuthManagerImpl;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.request.*;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid ;
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
    public String createProject(Authentication authentication, @Valid @RequestBody ProjectCreateRequest request) {
        String projectId = projectService.createProject(request, authManager.getPrincipal(authentication).getUserId());
        return projectId;
    }

    @Operation(summary = "Update a Project", description = "Update a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}")
    public Void updateProject(Authentication authentication ,@PathVariable("projectId") String projectId, @Valid @RequestBody ProjectUpdateRequest request) {
        projectService.updateProject(projectId, request, authManager.getPrincipal(authentication).getUserId());
        return null;
    }

    @Operation(summary = "Archive a Project", description = "Archive a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}/archive")
    public Void archiveProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.archiveProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return null;
    }

    @Operation(summary = "Unarchive a Project", description = "Unarchive a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}/unarchive")
    public Void unarchiveProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.unarchiveProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return null;
    }

    @Operation(summary = "Delete a Project", description = "Delete a Project", tags = { "DELETE" })
    @DeleteMapping("/{projectId}")
    public Void deleteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.deleteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return null;
    }

    @Operation(summary = "Favorite a Project", description = "Favorite a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}/favorite")
    public Void favoriteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.favoriteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return null;
    }

    @Operation(summary = "UnFavorite a Project", description = "UnFavorite a Project", tags = { "PATCH" })
    @PatchMapping("/{projectId}/unfavorite")
    public Void unFavoriteProject(Authentication authentication, @PathVariable("projectId") String projectId) {
        projectService.unFavoriteProject(projectId, authManager.getPrincipal(authentication).getUserId());
        return null;
    }

    @Operation(summary = "Get Project List", description = "Get Project List", tags = { "GET" })
    @GetMapping("")
    public List<ProjectItem> getProjectList(Authentication authentication, @Valid ProjectListRequest request) {
        UserDetailsImpl userDetails = authManager.getPrincipal(authentication);
        return projectService.getProjectList(userDetails.getUserId(), request);
    }

    @Operation(summary = "Get Project Select items", description = "Get Project Select items", tags = { "GET" })
    @GetMapping("/select")
    public List<SelectItem> getProjectSelectItems(Authentication authentication, @Valid ProjectRequest request) {
        List<SelectItem> selectItems = projectService.getProjectSelectItems(request, authManager.getPrincipal(authentication).getUserId());
        return selectItems;
    }

    @Operation(summary = "Get Notes Page by Project", description = "Get Notes Page by Project", tags = { "GET" })
    @GetMapping("/{projectId}/notes")
    public PageResult<NoteItem> getNotesByProject(Authentication authentication, @PathVariable("projectId") String projectId,  @Valid NotePageRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return projectService.getNotePageByProject(projectId, request, principal.getUserId());
    }

    @Operation(summary = "Get inbox notes", description = "Get inbox notes", tags = { "GET" })
    @GetMapping("/inbox/notes")
    public PageResult<NoteItem> getInboxNotes(Authentication authentication,  @Valid NotePageRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return projectService.getInboxNotePage(request, principal.getUserId());
    }
}

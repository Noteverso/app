package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.ProjectService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/projects")
@Slf4j
public class ProjectController {
    private final ProjectService projectService;
    @PostMapping("")
    public ApiResult<Void> createProject(Authentication authentication, @Valid @RequestBody ProjectCreateRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        projectService.createProject(request, principal.getUserId());
        return ApiResult.success(null);
    }
}

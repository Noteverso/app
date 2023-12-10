package com.noteverso.core.controller;

import com.noteverso.common.api.ApiResult;
import com.noteverso.core.request.ProjectCreateRequest;
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
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<Void> createProject(Authentication authentication, @Valid @RequestBody ProjectCreateRequest request) {
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        projectService.createProject(request, principal.getUserId());
        return ApiResult.success(null);
    }
}

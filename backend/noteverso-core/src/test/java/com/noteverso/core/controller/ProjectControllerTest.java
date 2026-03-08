package com.noteverso.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteverso.core.model.dto.NoteItem;
import com.noteverso.core.model.dto.ProjectItem;
import com.noteverso.core.model.entity.User;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.request.NotePageRequest;
import com.noteverso.core.model.request.ProjectCreateRequest;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {
    @Mock
    ProjectService projectService;

    MockMvc mockMvc;
    Authentication authentication;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        User user = User.builder().username("test@email.com").userId("12345").build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ProjectController(projectService))
                // set default request authentication
                .defaultRequest(get("/")
                        .with(request -> {
                            request.setUserPrincipal(authentication);
                            return request;
                        }
                ))
                .build();
    }

    @Test
    void should_createProjectSuccessfully() throws Exception {
        // Arrange
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();
        projectCreateRequest.setName("project");
        projectCreateRequest.setColor("red");
        projectCreateRequest.setChildOrder(1);

        // Act
       ResultActions result = mockMvc
               .perform(post("/api/v1/projects")
                        .content(objectMapper.writeValueAsString(projectCreateRequest))
                        .contentType("application/json"))
                .andDo(print());

      // Assert
      result.andExpect(status().isCreated());
    }

    @Test
    void createProject_shouldThrowException_whenRequestIsInvalid() throws Exception {
        // Arrange
        ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();
        projectCreateRequest.setColor("red");
        projectCreateRequest.setChildOrder(1);

        // Act
        ResultActions result = mockMvc
                .perform(post("/api/v1/projects")
                        .content(objectMapper.writeValueAsString(projectCreateRequest))
                        .contentType("application/json")
                )
                .andDo(print());

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    void should_getProjects_whenEmpty() throws Exception {
        // Arrange
        when(projectService.getProjectList(anyString(), any())).thenReturn(new ArrayList<>());

        // Act & Assert
        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void should_getProjectNotes_whenEmpty() throws Exception {
        // Arrange
        String projectId = "project123";
        PageResult<NoteItem> emptyResult = new PageResult<>();
        emptyResult.setRecords(new ArrayList<>());
        emptyResult.setTotal(0L);
        emptyResult.setPageIndex(1);
        emptyResult.setPageSize(10);

        when(projectService.getNotePageByProject(anyString(), any(NotePageRequest.class), anyString())).thenReturn(emptyResult);

        // Act & Assert
        mockMvc.perform(get("/api/v1/projects/{projectId}/notes", projectId)
                        .param("pageIndex", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.records").isEmpty());
    }

    @Test
    void should_getInboxNotes_whenEmpty() throws Exception {
        // Arrange
        PageResult<NoteItem> emptyResult = new PageResult<>();
        emptyResult.setRecords(new ArrayList<>());
        emptyResult.setTotal(0L);
        emptyResult.setPageIndex(1);
        emptyResult.setPageSize(10);

        when(projectService.getInboxNotePage(any(NotePageRequest.class), anyString())).thenReturn(emptyResult);

        // Act & Assert
        mockMvc.perform(get("/api/v1/projects/inbox/notes")
                        .param("pageIndex", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.records").isEmpty());
    }
}

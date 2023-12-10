package com.noteverso.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteverso.core.model.User;
import com.noteverso.core.request.ProjectCreateRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    void should_throwException_whenProjectCreateRequestIsInvalid() throws Exception {
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
}
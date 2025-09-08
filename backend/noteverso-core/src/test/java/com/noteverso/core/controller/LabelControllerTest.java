package com.noteverso.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteverso.core.model.entity.User;
import com.noteverso.core.model.request.LabelCreateRequest;
import com.noteverso.core.model.request.LabelUpdateRequest;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.LabelService;
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


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LabelControllerTest {
    @Mock
    LabelService labelService;

    MockMvc mockMvc;

    private Authentication authentication;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        User user = User.builder().username("test@email.com").userId("12345").build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new LabelController(labelService))
                .defaultRequest(get("/")
                        .with(request -> {
                            request.setUserPrincipal(authentication);
                            return request;
                        })
                )
                .build();
    }

    @Test
    void createLabel_shouldReturnStatusCreated() throws Exception {
        // Arrange
        LabelCreateRequest request = new LabelCreateRequest();
        request.setColor("red");
        request.setName("test");
        request.setIsFavorite(1);

        // Act
        ResultActions result = mockMvc
                .perform(post("/api/v1/labels")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType("application/json"))
                .andDo(print());

        // Assert
        result.andExpect(status().isCreated());
    }

    @Test
    void createLabel_shouldReturnStatusBadRequest() throws Exception {
        // Arrange
        LabelCreateRequest request = new LabelCreateRequest();
        request.setColor("red");
        request.setName("test");
        request.setIsFavorite(1);

        // Act
        ResultActions result = mockMvc
                .perform(post("/api/v1/labels"))
                .andDo(print());

        // Assert
        result.andExpect(status().isBadRequest());
    }

    @Test
    void updateLabel_shouldReturnStatusOk() throws Exception {
        // Arrange
        String labelId = "1234567";
        LabelUpdateRequest request = new LabelUpdateRequest();
        request.setName("test");
        request.setColor("red");
        request.setIsFavorite(1);

        // Act
        ResultActions result = mockMvc
                .perform(patch("/api/v1/labels/" + labelId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print());
        // Assert
        result.andExpect(status().isOk());
    }

    @Test
    void deleteLabel_shouldReturnStatusOk() throws Exception {
        // Arrange
        String labelId = "1234567";

        // Act
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/labels/" + labelId))
                .andDo(print());

        // Assert
        resultActions.andExpect(status().isOk());
    }

    @Test
    void favoriteLabel_shouldReturnStatusOk() throws Exception {
        // Arrange
        String labelId = "1234567";

        // Act
        ResultActions resultActions = mockMvc
                .perform(patch("/api/v1/labels/" + labelId + "/favorite"))
                .andDo(print());

        // Assert
        resultActions.andExpect(status().isOk());
    }

    @Test
    void unFavoriteLabel_shouldReturnStatusOk()  throws Exception {
        // Arrange
        String labelId = "1234567";

        // Act
        ResultActions resultActions = mockMvc
                .perform(patch("/api/v1/labels/" + labelId + "/unfavorite"))
                .andDo(print());

        // Assert
        resultActions.andExpect(status().isOk());
    }
}

package com.noteverso.core.controller;

import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.model.dto.NoteItem;
import com.noteverso.core.model.entity.User;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NoteControllerTest {

    @Mock
    private NoteService noteService;

    @Mock
    private AuthManager authManager;

    private MockMvc mockMvc;
    private Authentication authentication;

    @BeforeEach
    void setup() {
        User user = User.builder().username("test@email.com").userId("user123").build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new NoteController(noteService))
                .defaultRequest(get("/")
                        .with(request -> {
                            request.setUserPrincipal(authentication);
                            return request;
                        })
                )
                .build();
    }

    @Test
    void should_searchNotes_successfully() throws Exception {
        // Arrange
        PageResult<NoteItem> pageResult = new PageResult<>();
        pageResult.setRecords(new ArrayList<>());
        pageResult.setTotal(0L);
        pageResult.setPageIndex(1);
        pageResult.setPageSize(10);

        when(noteService.searchNotes(anyString(), anyString(), any(), any(), any(), any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(pageResult);

        // Act & Assert
        mockMvc.perform(get("/api/v1/notes/search")
                        .param("keyword", "test")
                        .param("pageIndex", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.pageIndex").value(1));
    }

    @Test
    void should_searchNotes_withLabels() throws Exception {
        // Arrange
        PageResult<NoteItem> pageResult = new PageResult<>();
        pageResult.setRecords(new ArrayList<>());
        pageResult.setTotal(0L);

        when(noteService.searchNotes(anyString(), isNull(), anyList(), any(), any(), any(), anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(pageResult);

        // Act & Assert
        mockMvc.perform(get("/api/v1/notes/search")
                        .param("labelIds", "label1,label2")
                        .param("pageIndex", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk());
    }
}
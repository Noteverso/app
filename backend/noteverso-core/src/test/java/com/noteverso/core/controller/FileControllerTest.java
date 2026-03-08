package com.noteverso.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteverso.core.manager.AuthManager;
import com.noteverso.core.manager.UserConfigManager;
import com.noteverso.core.model.dto.AttachmentDTO;
import com.noteverso.core.model.entity.User;
import com.noteverso.core.model.entity.UserConfig;
import com.noteverso.core.model.pagination.PageRequest;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.request.AttachmentRequest;
import com.noteverso.core.security.service.UserDetailsImpl;
import com.noteverso.core.service.AttachmentService;
import com.noteverso.core.service.component.OssClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FileControllerTest {

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private OssClient ossClient;

    @Mock
    private AuthManager authManager;

    @Mock
    private UserConfigManager userConfigManager;

    private MockMvc mockMvc;
    private Authentication authentication;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        User user = User.builder().username("test@email.com").userId("user123").build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        this.authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);

        when(authManager.getPrincipal(any(Authentication.class))).thenReturn(userDetails);

        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new FileController(ossClient, authManager, userConfigManager, attachmentService))
                .defaultRequest(get("/")
                        .with(request -> {
                            request.setUserPrincipal(authentication);
                            return request;
                        })
                )
                .build();
    }

    @Test
    void should_saveAttachment_successfully() throws Exception {
        // Arrange
        AttachmentRequest request = new AttachmentRequest();
        request.setName("test.pdf");
        request.setContentType("application/pdf");
        request.setContentLength(1024L);
        request.setUrl("s3://bucket/test.pdf");
        request.setResourceType("file");

        when(attachmentService.createAttachment(any(AttachmentDTO.class), anyString())).thenReturn("att123");

        // Act & Assert
        mockMvc.perform(post("/api/v1/files/attachments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(attachmentService).createAttachment(any(AttachmentDTO.class), eq("user123"));
    }

    @Test
    void should_getUserAttachments_successfully() throws Exception {
        // Arrange
        PageResult<AttachmentDTO> pageResult = new PageResult<>();
        AttachmentDTO dto = new AttachmentDTO();
        dto.setAttachmentId("att123");
        dto.setName("test.pdf");
        pageResult.setRecords(new ArrayList<>());
        pageResult.getRecords().add(dto);
        pageResult.setTotal(1L);
        pageResult.setPageIndex(1);
        pageResult.setPageSize(10);

        when(attachmentService.getUserAttachments(anyString(), any(PageRequest.class))).thenReturn(pageResult);

        // Act & Assert
        mockMvc.perform(get("/api/v1/files/attachments")
                        .param("pageIndex", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.records[0].name").value("test.pdf"));
    }

    @Test
    void should_getUserAttachments_whenEmpty() throws Exception {
        // Arrange
        PageResult<AttachmentDTO> emptyResult = new PageResult<>();
        emptyResult.setRecords(new ArrayList<>());
        emptyResult.setTotal(0L);
        emptyResult.setPageIndex(1);
        emptyResult.setPageSize(10);

        when(attachmentService.getUserAttachments(anyString(), any(PageRequest.class))).thenReturn(emptyResult);

        // Act & Assert
        mockMvc.perform(get("/api/v1/files/attachments")
                        .param("pageIndex", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.records").isEmpty());
    }

    @Test
    void should_getPresignedUrl_successfully() throws Exception {
        // Arrange
        String attachmentId = "att123";
        String presignedUrl = "https://s3.amazonaws.com/bucket/test.pdf?signature=xyz";

        when(attachmentService.getPreviewSignature(eq(attachmentId), anyString())).thenReturn(presignedUrl);

        // Act & Assert
        mockMvc.perform(get("/api/v1/files/{attachmentId}", attachmentId))
                .andExpect(status().isOk());

        verify(attachmentService).getPreviewSignature(eq(attachmentId), eq("user123"));
    }

    @Test
    void should_deleteAttachment_successfully() throws Exception {
        // Arrange
        String attachmentId = "att123";

        // Act & Assert
        mockMvc.perform(delete("/api/v1/files/attachments/{attachmentId}", attachmentId))
                .andExpect(status().isOk());

        verify(attachmentService).deleteAttachment(eq(attachmentId), eq("user123"));
    }
}

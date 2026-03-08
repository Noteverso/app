package com.noteverso.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.core.dao.AttachmentMapper;
import com.noteverso.core.model.dto.AttachmentDTO;
import com.noteverso.core.model.entity.Attachment;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.pagination.PageRequest;
import com.noteverso.core.service.component.OssClient;
import com.noteverso.core.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private OssClient ossClient;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @Test
    void should_createAttachment_successfully() {
        // Arrange
        String userId = "user123";
        AttachmentDTO dto = new AttachmentDTO();
        dto.setName("test.pdf");
        dto.setType("application/pdf");
        dto.setSize(1024L);
        dto.setUrl("s3://bucket/test.pdf");
        dto.setResourceType("file");

        // Act
        String attachmentId = attachmentService.createAttachment(dto, userId);

        // Assert
        assertThat(attachmentId).isNotNull();
        ArgumentCaptor<Attachment> captor = ArgumentCaptor.forClass(Attachment.class);
        verify(attachmentMapper).insert(captor.capture());
        Attachment saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("test.pdf");
        assertThat(saved.getCreator()).isEqualTo(userId);
    }

    @Test
    void should_getUserAttachments_withPagination() {
        // Arrange
        String userId = "user123";
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPageIndex(1L);
        pageRequest.setPageSize(10L);

        List<Attachment> attachments = new ArrayList<>();
        Attachment attachment = new Attachment();
        attachment.setAttachmentId("att123");
        attachment.setName("test.pdf");
        attachment.setType("application/pdf");
        attachment.setSize(1024L);
        attachment.setUrl("s3://bucket/test.pdf");
        attachment.setResourceType("file");
        attachment.setAddedAt(Instant.now());
        attachments.add(attachment);

        Page<Attachment> page = new Page<>(1, 10);
        page.setRecords(attachments);
        page.setTotal(1);

        when(attachmentMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        // Act
        PageResult<AttachmentDTO> result = attachmentService.getUserAttachments(userId, pageRequest);

        // Assert
        assertThat(result.getRecords()).hasSize(1);
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords().get(0).getName()).isEqualTo("test.pdf");
    }

    @Test
    void should_deleteAttachment_successfully() {
        // Arrange
        String userId = "user123";
        String attachmentId = "att123";

        Attachment attachment = new Attachment();
        attachment.setAttachmentId(attachmentId);
        attachment.setUrl("s3://bucket/test.pdf");

        when(attachmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(attachment);
        when(attachmentMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        // Act
        attachmentService.deleteAttachment(attachmentId, userId);

        // Assert
        verify(attachmentMapper).delete(any(LambdaQueryWrapper.class));
        verify(ossClient).delete("s3://bucket/test.pdf");
    }

    @Test
    void should_throwException_whenAttachmentNotFound() {
        // Arrange
        String userId = "user123";
        String attachmentId = "att123";

        when(attachmentMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // Act & Assert
        assertThatThrownBy(() -> attachmentService.deleteAttachment(attachmentId, userId))
                .isInstanceOf(NoSuchDataException.class)
                .hasMessageContaining("Attachment not found");
    }

    @Test
    void should_calculateTotalSize_correctly() {
        // Arrange
        String userId = "user123";
        List<Attachment> attachments = new ArrayList<>();
        
        Attachment att1 = new Attachment();
        att1.setSize(1024L);
        attachments.add(att1);
        
        Attachment att2 = new Attachment();
        att2.setSize(2048L);
        attachments.add(att2);

        when(attachmentMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(attachments);

        // Act
        Long totalSize = attachmentService.userAttachmentTotalSize(userId);

        // Assert
        assertThat(totalSize).isEqualTo(3072L);
    }

    @Test
    void should_createAttachments_notCallMapper_whenEmptyList() {
        // Arrange
        List<AttachmentDTO> emptyList = List.of();
        String userId = "user1";

        // Act
        attachmentService.createAttachments(emptyList, userId);

        // Assert
        verify(attachmentMapper, never()).batchInsert(any());
    }

    @Test
    void should_createAttachments_notCallMapper_whenNull() {
        // Arrange
        String userId = "user1";

        // Act
        attachmentService.createAttachments(null, userId);

        // Assert
        verify(attachmentMapper, never()).batchInsert(any());
    }
}
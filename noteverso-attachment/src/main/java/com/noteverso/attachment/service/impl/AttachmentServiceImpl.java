package com.noteverso.attachment.service.impl;

import com.noteverso.attachment.dao.AttachmentMapper;
import com.noteverso.attachment.dto.AttachmentDTO;
import com.noteverso.attachment.model.Attachment;
import com.noteverso.attachment.service.IAttachmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements IAttachmentService {
    private final AttachmentMapper attachmentMapper;

    public void createAttachment(AttachmentDTO request) {
        Attachment attachment = Attachment
                .builder()
                .name(request.getName())
                .type(request.getType())
                .resourceType(request.getResourceType())
                .url(request.getUrl())
                .noteId(null != request.getNoteId() ? request.getNoteId() : null)
                .projectId(null != request.getProjectId() ? request.getProjectId() : null)
                .commentId(null != request.getCommentId() ? request.getCommentId() : null)
                .creator(1L)
                .updater(1L)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        attachmentMapper.insert(attachment);
    }

    public void createMultipleAttachments(List<AttachmentDTO> request) {

    }
}

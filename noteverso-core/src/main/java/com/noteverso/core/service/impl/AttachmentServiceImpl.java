package com.noteverso.core.service.impl;

import com.noteverso.common.context.TenantContext;
import com.noteverso.core.dao.AttachmentMapper;
import com.noteverso.core.dto.AttachmentDTO;
import com.noteverso.core.model.Attachment;
import com.noteverso.core.service.IAttachmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements IAttachmentService {
    private final AttachmentMapper attachmentMapper;

    public void createAttachment(AttachmentDTO request) {
        String tenantId = TenantContext.getTenantId();
        Attachment attachment = construcAttachment(request, tenantId);
        attachmentMapper.insert(attachment);
    }

    public void createMultipleAttachments(List<AttachmentDTO> request) {
        String tenantId = TenantContext.getTenantId();
        List<Attachment> attachments = new ArrayList<>();
        for (AttachmentDTO file : request) {
            Attachment attachment = construcAttachment(file, tenantId);
            attachments.add(attachment);
        }
        if (!attachments.isEmpty()) {
            attachmentMapper.batchInsert(attachments);
        }

    }

    private Attachment construcAttachment(AttachmentDTO file, String tenantId) {
        return Attachment
                .builder()
                .name(file.getName())
                .type(file.getType())
                .resourceType(file.getResourceType())
                .url(file.getUrl())
                .size(file.getSize())
                .noteId(null != file.getNoteId() ? file.getNoteId() : null)
                .projectId(null != file.getProjectId() ? file.getProjectId() : null)
                .commentId(null != file.getCommentId() ? file.getCommentId() : null)
                .creator(tenantId)
                .updater(tenantId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}

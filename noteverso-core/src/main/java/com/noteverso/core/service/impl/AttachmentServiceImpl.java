package com.noteverso.core.service.impl;

import com.noteverso.core.dao.AttachmentMapper;
import com.noteverso.core.dto.AttachmentDTO;
import com.noteverso.core.model.Attachment;
import com.noteverso.core.service.AttachmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentMapper attachmentMapper;

    @Override
    public void createAttachment(AttachmentDTO request, String tenantId ) {
        Attachment attachment = construcAttachment(request, tenantId);
        attachmentMapper.insert(attachment);
    }

    @Override
    public void createAttachments(List<AttachmentDTO> request, String tenantId) {
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
                .attachmentId(file.getAttachmentId())
                .creator(tenantId)
                .updater(tenantId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public void deleteAttachments(String attachmentId) {};
}

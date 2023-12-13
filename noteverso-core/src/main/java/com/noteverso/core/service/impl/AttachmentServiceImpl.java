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
    public void createAttachment(AttachmentDTO request, String userId ) {
        Attachment attachment = construcAttachment(request, userId);
        attachmentMapper.insert(attachment);
    }

    @Override
    public void createAttachments(List<AttachmentDTO> request, String userId) {
        List<Attachment> attachments = new ArrayList<>();
        for (AttachmentDTO file : request) {
            Attachment attachment = construcAttachment(file, userId);
            attachments.add(attachment);
        }
        if (!attachments.isEmpty()) {
            attachmentMapper.batchInsert(attachments);
        }

    }

    private Attachment construcAttachment(AttachmentDTO file, String userId) {
        return Attachment
                .builder()
                .name(file.getName())
                .type(file.getType())
                .resourceType(file.getResourceType())
                .url(file.getUrl())
                .size(file.getSize())
                .attachmentId(file.getAttachmentId())
                .creator(userId)
                .updater(userId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public void deleteAttachments(String attachmentId) {};
}

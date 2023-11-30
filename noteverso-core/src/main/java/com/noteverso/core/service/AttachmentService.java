package com.noteverso.core.service;

import com.noteverso.core.dto.AttachmentDTO;

import java.util.List;

public interface AttachmentService {
    void createAttachment(AttachmentDTO request, String tenantId);

    void createAttachments(List<AttachmentDTO> request, String tenantId);

    void deleteAttachments(String attachmentId);
}

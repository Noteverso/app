package com.noteverso.core.service;

import com.noteverso.core.dto.AttachmentDTO;

import java.util.List;

public interface AttachmentService {
    void createAttachment(AttachmentDTO request, String userId);

    void createAttachments(List<AttachmentDTO> request, String userId);

    void deleteAttachments(String attachmentId);
}

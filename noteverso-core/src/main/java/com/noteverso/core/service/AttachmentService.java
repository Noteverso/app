package com.noteverso.core.service;

import com.noteverso.core.dto.AttachmentDTO;
import com.noteverso.core.request.AttachmentRequest;

import java.util.List;

public interface AttachmentService {
    String createAttachment(AttachmentDTO attachmentDTO, String userId);

    Long userAttachmentTotalSize(String userId);

    String getPreviewSignature(String attachmentId, String userId);

    void createAttachments(List<AttachmentDTO> request, String userId);

    void deleteAttachments(String attachmentId);
}

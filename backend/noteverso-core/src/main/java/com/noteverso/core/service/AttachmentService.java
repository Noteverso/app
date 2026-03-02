package com.noteverso.core.service;

import com.noteverso.core.model.dto.AttachmentDTO;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.pagination.PageRequest;

import java.util.List;

public interface AttachmentService {
    String createAttachment(AttachmentDTO attachmentDTO, String userId);

    Long userAttachmentTotalSize(String userId);

    String getPreviewSignature(String attachmentId, String userId);

    void createAttachments(List<AttachmentDTO> request, String userId);

    void deleteAttachments(String attachmentId);

    PageResult<AttachmentDTO> getUserAttachments(String userId, PageRequest pageRequest);

    void deleteAttachment(String attachmentId, String userId);
}

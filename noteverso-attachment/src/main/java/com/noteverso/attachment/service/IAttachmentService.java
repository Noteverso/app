package com.noteverso.attachment.service;

import com.noteverso.attachment.dto.AttachmentDTO;

import java.util.List;

public interface IAttachmentService {
    void createAttachment(AttachmentDTO request);

    void createMultipleAttachments(List<AttachmentDTO> request);
}

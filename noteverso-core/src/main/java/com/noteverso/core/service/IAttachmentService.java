package com.noteverso.core.service;

import com.noteverso.core.dto.AttachmentDTO;

import java.util.List;

public interface IAttachmentService {
    void createAttachment(AttachmentDTO request);

    void createMultipleAttachments(List<AttachmentDTO> request);
}

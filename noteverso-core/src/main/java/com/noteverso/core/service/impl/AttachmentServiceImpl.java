package com.noteverso.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.AttachmentMapper;
import com.noteverso.core.model.dto.AttachmentDTO;
import com.noteverso.core.model.entity.Attachment;
import com.noteverso.core.service.AttachmentService;
import com.noteverso.core.service.component.OssClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.noteverso.common.constant.NumConstants.*;

@Service
@AllArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentMapper attachmentMapper;
    private final OssClient ossClient;

    private static final SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            ATTACHMENT_DATACENTER_ID, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Override
    public String createAttachment(AttachmentDTO attachmentDTO, String userId ) {
        String attachmentId = String.valueOf(snowFlakeUtils.nextId());

        Attachment attachment = construcAttachment(attachmentDTO, userId);
        attachment.setAttachmentId(attachmentId);
        attachmentMapper.insert(attachment);

        return attachmentId;
    }

    @Override
    public Long userAttachmentTotalSize(String userId) {
        LambdaQueryWrapper<Attachment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attachment::getCreator, userId);
        List<Attachment> attachments = attachmentMapper.selectList(queryWrapper);
        long totalSize = 0L;

        for (Attachment attachment : attachments) {
            totalSize += attachment.getSize();
        }

        return totalSize;
    }

    @Override
    public String getPreviewSignature(String attachmentId, String userId) {
        LambdaQueryWrapper<Attachment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attachment::getAttachmentId, attachmentId);
        queryWrapper.eq(Attachment::getCreator, userId);
        Attachment attachment =  attachmentMapper.selectOne(queryWrapper);
        if (attachment == null) {
            return null;
        }

        return ossClient.getPrivateUrl(attachment.getUrl(), 60 * 5);
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

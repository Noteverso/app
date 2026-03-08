package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.entity.Attachment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AttachmentMapperTest {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Test
    void should_batchInsert_successfully() {
        // Arrange
        Attachment att1 = constructAttachment("att1", "file1.pdf", "user1");
        Attachment att2 = constructAttachment("att2", "file2.jpg", "user1");
        List<Attachment> attachments = List.of(att1, att2);

        // Act
        attachmentMapper.batchInsert(attachments);

        // Assert
        List<Attachment> allAttachments = attachmentMapper.selectList(null);
        assertThat(allAttachments).hasSizeGreaterThanOrEqualTo(2);
        assertThat(allAttachments).extracting(Attachment::getAttachmentId)
            .contains("att1", "att2");
        assertThat(allAttachments).extracting(Attachment::getName)
            .contains("file1.pdf", "file2.jpg");
    }

    private Attachment constructAttachment(String attachmentId, String name, String userId) {
        Attachment attachment = new Attachment();
        attachment.setAttachmentId(attachmentId);
        attachment.setName(name);
        attachment.setSize(1024L);
        attachment.setType("application/pdf");
        attachment.setUrl("/path/to/file");
        attachment.setResourceType("file");
        attachment.setCreator(userId);
        attachment.setUpdater(userId);
        attachment.setAddedAt(Instant.now());
        attachment.setUpdatedAt(Instant.now());
        return attachment;
    }
}

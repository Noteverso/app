package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.dto.AttachmentCount;
import com.noteverso.core.model.entity.AttachmentRelation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AttachmentRelationMapperTest {
    @Autowired
    AttachmentRelationMapper attachmentRelationMapper;

    @Test
    void getAttachmentCountByObjectIds() {
        // Arrange
        String userId = "1";
        List<String> attachmentIds = List.of("1", "2", "3", "4", "5");
        List<AttachmentRelation> attachmentRelations = new ArrayList<>();
        for (String attachmentId : attachmentIds) {
            Instant now = Instant.now();
            AttachmentRelation relation = new AttachmentRelation();
            relation.setAttachmentId(attachmentId);
            relation.setObjectId("123");
            relation.setCreator(userId);
            relation.setUpdater(userId);
            relation.setAddedAt(now);
            relation.setUpdatedAt(now);
            attachmentRelations.add(relation);
        }

        attachmentRelationMapper.batchInsert(attachmentRelations);

        // Act
        List<AttachmentCount> result = attachmentRelationMapper.getAttachmentCountByObjectIds(List.of("123"), userId);

        // Assert
        assertEquals(attachmentIds.size(), result.get(0).getAttachmentCount());
        assertEquals("123", result.get(0).getObjectId());
    }
}

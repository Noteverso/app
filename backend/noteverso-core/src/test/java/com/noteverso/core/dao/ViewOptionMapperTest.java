package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.entity.ViewOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ViewOptionMapperTest {

    @Autowired
    private ViewOptionMapper viewOptionMapper;

    @Test
    void should_batchSelectByObjectIds_successfully() {
        // Arrange
        ViewOption vo1 = constructViewOption("obj1", 1, "user1");
        ViewOption vo2 = constructViewOption("obj2", 1, "user1");
        ViewOption vo3 = constructViewOption("obj3", 1, "user2");
        viewOptionMapper.insert(vo1);
        viewOptionMapper.insert(vo2);
        viewOptionMapper.insert(vo3);

        // Act
        List<ViewOption> found = viewOptionMapper.batchSelectByObjectIds(
            List.of("obj1", "obj2", "obj3"), "user1"
        );

        // Assert
        assertThat(found).hasSize(2);
        assertThat(found).extracting(ViewOption::getObjectId)
            .containsExactlyInAnyOrder("obj1", "obj2");
    }

    @Test
    void should_batchSelectByObjectIds_returnEmpty_whenNoMatch() {
        // Act
        List<ViewOption> found = viewOptionMapper.batchSelectByObjectIds(
            List.of("nonexistent"), "user1"
        );

        // Assert
        assertThat(found).isEmpty();
    }

    private ViewOption constructViewOption(String objectId, Integer viewType, String userId) {
        ViewOption vo = new ViewOption();
        vo.setObjectId(objectId);
        vo.setViewType(viewType);
        vo.setViewMode(0);
        vo.setGroupedBy(0);
        vo.setOrderedBy(0);
        vo.setOrderValue(0);
        vo.setShowArchived(0);
        vo.setShowPinned(0);
        vo.setShowDeleted(0);
        vo.setCreator(userId);
        vo.setUpdater(userId);
        vo.setAddedAt(Instant.now());
        vo.setUpdateAt(Instant.now());
        return vo;
    }
}

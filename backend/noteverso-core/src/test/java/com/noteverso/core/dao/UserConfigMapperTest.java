package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.entity.UserConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserConfigMapperTest {

    @Autowired
    private UserConfigMapper userConfigMapper;

    @Test
    void should_findUserConfigByUserId_successfully() {
        // Arrange
        UserConfig config = constructUserConfig("user1", "inbox1");
        userConfigMapper.insert(config);

        // Act
        UserConfig found = userConfigMapper.findUserConfigByUserId("user1");

        // Assert
        assertThat(found).isNotNull();
        assertThat(found.getUserId()).isEqualTo("user1");
        assertThat(found.getInboxProjectId()).isEqualTo("inbox1");
    }

    @Test
    void should_findUserConfigByUserId_returnNull_whenNotFound() {
        // Act
        UserConfig found = userConfigMapper.findUserConfigByUserId("nonexistent");

        // Assert
        assertThat(found).isNull();
    }

    private UserConfig constructUserConfig(String userId, String inboxProjectId) {
        UserConfig config = new UserConfig();
        config.setUserId(userId);
        config.setInboxProjectId(inboxProjectId);
        config.setLang(0);
        config.setStartPage("project/" + inboxProjectId);
        config.setThemeId(0);
        config.setMaxFileSize(10485760L);
        config.setFilesSizeQuota(104857600L);
        config.setProjectsQuota(100L);
        config.setLinkedNotesQuota(50L);
        config.setCreator(userId);
        config.setUpdater(userId);
        config.setAddedAt(Instant.now());
        config.setUpdatedAt(Instant.now());
        return config;
    }
}

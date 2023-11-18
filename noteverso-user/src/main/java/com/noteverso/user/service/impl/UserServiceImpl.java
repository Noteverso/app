package com.noteverso.user.service.impl;

import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.project.dao.ProjectMapper;
import com.noteverso.project.service.IProjectService;
import com.noteverso.user.constant.StringConstants;
import com.noteverso.user.dao.UserConfigMapper;
import com.noteverso.user.dao.UserMapper;
import com.noteverso.user.model.User;
import com.noteverso.user.model.UserConfig;
import com.noteverso.user.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.noteverso.common.constant.NumberConstants.*;
import static com.noteverso.user.constant.NumConstants.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements IUserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final IProjectService projectService;
    private final ProjectMapper projectMapper;
    private final UserConfigMapper userConfigMapper;
    private static final SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            1L, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createUser(String username, String password) {
        String userId = String.valueOf(snowFlakeUtils.nextId());
        var user = constructUser(userId, username, password);
        userMapper.insert(user);

        var project = projectService.constructInboxProject(userId);
        projectMapper.insert(project);

        var userConfig = constructUserConfig(userId, project.getProjectId());
        userConfigMapper.insert(userConfig);
    }

    private User constructUser(String userId, String username, String password) {
        return User
            .builder()
            .userId(userId)
            .email(username)
            .username(username)
            .authority(StringConstants.AUTHORITY_NORMAL)
            .password(passwordEncoder.encode(password))
            .hasPassword(NUM_1)
            .premiumStatus(NUM_O)
            .isPremium(NUM_O)
            .joinedAt(Instant.now())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    private UserConfig constructUserConfig(String userId, String inboxProjectId) {
        return UserConfig
                .builder()
                .userId(userId)
                .inboxProjectId(inboxProjectId)
                .lang(NUM_O)
                .startPage("project/" + inboxProjectId)
                .themeId(NUM_O)
                .creator(userId)
                .maxFileSize(MAX_FILE_SIZE_NORMAL)
                .fileSizeQuota(FILE_SIZE_QUOTA_NORMAL)
                .projectQuota(PROJECT_QUOTA_NORMAL)
                .linkedNoteQuota(LINKED_NOTE_QUOTA_NORMAL)
                .updater(userId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}

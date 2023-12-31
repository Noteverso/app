package com.noteverso.core.service.impl;

import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.constant.StringConstants;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dao.UserConfigMapper;
import com.noteverso.core.dao.UserMapper;
import com.noteverso.core.enums.ObjectViewTypeEnum;
import com.noteverso.core.model.User;
import com.noteverso.core.model.UserConfig;
import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.service.ProjectService;
import com.noteverso.core.service.UserService;
import com.noteverso.core.service.ViewOptionService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.noteverso.common.constant.NumConstants.*;
import static com.noteverso.core.constant.NumConstants.*;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final UserConfigMapper userConfigMapper;
    private final ViewOptionService viewOptionService;
    private static final SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            USER_DATACENTER_ID, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createUser(String username, String password) {
        // Create user
        String userId = String.valueOf(snowFlakeUtils.nextId());
        var user = constructUser(userId, username, password);
        userMapper.insert(user);

        // Create default project - inbox
        var project = projectService.constructInboxProject(userId);
        projectMapper.insert(project);

        // Create user config - project quota and other
        var userConfig = constructUserConfig(userId, project.getProjectId());
        userConfigMapper.insert(userConfig);

        // Create default view option for today, upcoming, attachment
        ViewOptionCreate viewOptionCreate = new ViewOptionCreate();
        viewOptionCreate.setViewType(ObjectViewTypeEnum.TODAY.getValue());
        viewOptionService.createViewOption(viewOptionCreate, userId);
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
                .filesSizeQuota(FILE_SIZE_QUOTA_NORMAL)
                .projectsQuota(PROJECT_QUOTA_NORMAL)
                .linkedNotesQuota(LINKED_NOTE_QUOTA_NORMAL)
                .updater(userId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}

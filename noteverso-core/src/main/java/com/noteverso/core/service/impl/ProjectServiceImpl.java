package com.noteverso.core.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.common.exceptions.BusinessException;
import com.noteverso.common.util.IPUtils;

import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dao.UserConfigMapper;
import com.noteverso.core.dao.UserMapper;
import com.noteverso.core.dto.ProjectDTO;
import com.noteverso.core.enums.ObjectViewTypeEnum;
import com.noteverso.core.manager.UserConfigManager;
import com.noteverso.core.model.Project;
import com.noteverso.core.model.UserConfig;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.service.ProjectService;
import com.noteverso.core.service.UserService;
import com.noteverso.core.service.ViewOptionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.noteverso.common.constant.NumConstants.*;



@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final ViewOptionService viewOptionService;
    private final UserConfigManager userConfigManager;

    private final static SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            PROJECT_DATACENTER_ID, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createProject(ProjectCreateRequest request, String tenantId) {
        String projectId = String.valueOf(snowFlakeUtils.nextId());
        long projectCount = getProjectCount(tenantId);
        long projectQuota = getProjectQuota(tenantId);

        if (projectCount + 1 > projectQuota) {
            throw new BusinessException("The project quota has been reached");
        }

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(projectId);
        projectDTO.setName(request.getName());
        projectDTO.setColor(request.getColor());
        projectDTO.setChildOrder(request.getChildOrder());
        projectDTO.setParentId(request.getParentId());
        projectDTO.setIsFavorite(null != request.getIsFavorite() ? request.getIsFavorite() : 0);
        projectDTO.setTenantId(tenantId);
        Project project = constructProject(projectDTO);
        projectMapper.insert(project);

        ViewOptionCreate viewOptionCreate = new ViewOptionCreate();
        viewOptionCreate.setObjectId(projectId);
        viewOptionCreate.setViewType(ObjectViewTypeEnum.PROJECT.getValue());
        viewOptionService.createViewOption(viewOptionCreate, tenantId);
    }

    public long getProjectQuota(String userId) {
        UserConfig userConfig = userConfigManager.getUserConfig(userId);
        if (userConfig != null) {
            return userConfig.getProjectsQuota();
        }
        return NUM_0L;
    }

    public long getProjectCount(String userId) {
        LambdaQueryWrapper<Project> projectQw = new LambdaQueryWrapper<>();
        projectQw.eq(Project::getCreator, userId);
        Long count = projectMapper.selectCount(projectQw);
        return null != count ? count : NUM_0L;
    }

    @Override
    public Project constructInboxProject(String userId) {
        String projectId = String.valueOf(snowFlakeUtils.nextId());

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(projectId);
        projectDTO.setIsInboxProject(NUM_1);
        projectDTO.setName("Inbox");
        projectDTO.setColor("white");
        projectDTO.setChildOrder(NUM_O);
        projectDTO.setTenantId(userId);

        return constructProject(projectDTO);
    }

    @Override
    public Project constructProject(ProjectDTO projectDTO) {
        return Project
                .builder()
                .projectId(projectDTO.getProjectId())
                .name(projectDTO.getName())
                .color(projectDTO.getColor())
                .childOrder(projectDTO.getChildOrder())
                .parentId(projectDTO.getParentId() != null ? projectDTO.getParentId() : null)
                .isFavorite(projectDTO.getIsFavorite() != null ? projectDTO.getIsFavorite() : NUM_O)
                .isCollapsed(NUM_O)
                .isInboxProject(projectDTO.getIsInboxProject() != null ? projectDTO.getIsInboxProject() : NUM_O)
                .isShared(NUM_O)
                .isArchived(NUM_O)
                .creator(projectDTO.getTenantId())
                .updater(projectDTO.getTenantId())
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }
}

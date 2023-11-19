package com.noteverso.core.service.impl;

import com.noteverso.common.context.TenantContext;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dto.ProjectDTO;
import com.noteverso.core.model.Project;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.service.IProjectService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.noteverso.common.constant.NumConstants.*;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements IProjectService {
    private final ProjectMapper projectMapper;
    private final static SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            1L, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Override
    public void createProject(ProjectCreateRequest request) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(request.getName());
        projectDTO.setColor(request.getColor());
        projectDTO.setChildOrder(request.getChildOrder());
        projectDTO.setParentId(request.getParentId());
        projectDTO.setIsFavorite(request.getIsFavorite());
        projectDTO.setViewStyle(request.getViewStyle());
        projectDTO.setTenantId(TenantContext.getTenantId());
        Project project = constructProject(projectDTO);
        projectMapper.insert(project);
    };

    @Override
    public Project constructInboxProject(String userId) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setIsInboxProject(NUM_1);
        projectDTO.setName("收件箱");
        projectDTO.setColor("white");
        projectDTO.setChildOrder(NUM_O);
        projectDTO.setTenantId(userId);

        return constructProject(projectDTO);
    }

    @Override
    public Project constructProject(ProjectDTO projectDTO) {
        String projectId = String.valueOf(snowFlakeUtils.nextId());
        return Project
                .builder()
                .projectId(projectId)
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

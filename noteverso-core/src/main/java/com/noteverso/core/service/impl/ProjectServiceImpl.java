package com.noteverso.core.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.common.exceptions.BusinessException;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.common.util.IPUtils;

import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dto.ProjectDTO;
import com.noteverso.core.dto.ProjectItem;
import com.noteverso.core.dto.ProjectViewOption;
import com.noteverso.core.dto.SelectItem;
import com.noteverso.core.enums.ObjectViewTypeEnum;
import com.noteverso.core.manager.NoteManager;
import com.noteverso.core.manager.UserConfigManager;
import com.noteverso.core.model.Label;
import com.noteverso.core.model.Project;
import com.noteverso.core.model.UserConfig;
import com.noteverso.core.model.ViewOption;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.request.ProjectRequest;
import com.noteverso.core.request.ProjectUpdateRequest;
import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.service.ProjectService;
import com.noteverso.core.service.ViewOptionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.noteverso.common.constant.NumConstants.*;
import static com.noteverso.core.constant.ExceptionConstants.PROJECT_NOT_FOUND;


@Service
@AllArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final ViewOptionService viewOptionService;
    private final UserConfigManager userConfigManager;
    private final NoteMapper noteMapper;
    private final NoteManager noteManager;

    private final static SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            PROJECT_DATACENTER_ID, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createProject(ProjectCreateRequest request, String userId) {
        String projectId = String.valueOf(snowFlakeUtils.nextId());
        long projectCount = getProjectCount(userId);
        long projectQuota = getProjectQuota(userId);

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
        projectDTO.setUserId(userId);
        Project project = constructProject(projectDTO);
        projectMapper.insert(project);

        ViewOptionCreate viewOptionCreate = new ViewOptionCreate();
        viewOptionCreate.setObjectId(projectId);
        viewOptionCreate.setViewType(ObjectViewTypeEnum.PROJECT.getValue());
        viewOptionService.createViewOption(viewOptionCreate, userId);

        return projectId;
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
        projectDTO.setUserId(userId);

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
                .creator(projectDTO.getUserId())
                .updater(projectDTO.getUserId())
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Override
    public void updateProject(String projectId, ProjectUpdateRequest request, String userId) {
        LambdaUpdateWrapper<Project> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Project::getProjectId, projectId);
        updateWrapper.eq(Project::getCreator, userId);

        Project project = new Project();
        project.setName(request.getName());
        project.setColor(request.getColor());
        project.setUpdatedAt(Instant.now());
        if (null != request.getIsFavorite()) {
            project.setIsFavorite(request.getIsFavorite());
        }

        int result = projectMapper.update(project, updateWrapper);
        if (result == 0) {
            throw new NoSuchDataException(PROJECT_NOT_FOUND);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archiveProject(String projectId, String userId) {
        updateProjectIsArchived(projectId, userId, NUM_1);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unarchiveProject(String projectId, String userId) {
        updateProjectIsArchived(projectId, userId, NUM_O);
    }

    public void updateProjectIsArchived(String projectId, String userId, Integer isArchived) {
        LambdaUpdateWrapper<Project> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Project::getProjectId, projectId);
        updateWrapper.eq(Project::getCreator, userId);

        Project project = new Project();
        project.setIsArchived(isArchived);
        project.setUpdatedAt(Instant.now());

        // archive project
        int result = projectMapper.update(project, updateWrapper);

        // archive notes of the project
        if (result > 0) {
            noteMapper.updateNoteIsArchivedByProject(projectId, userId, isArchived);
        } else {
            throw new NoSuchDataException(PROJECT_NOT_FOUND);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(String projectId, String userId) {
        LambdaUpdateWrapper<Project> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Project::getProjectId, projectId);
        updateWrapper.eq(Project::getCreator, userId);

        int result = projectMapper.delete(updateWrapper);

        // delete notes of the project
        if (result > 0) {
            noteMapper.updateNotesIsDeletedByProject(projectId, userId);
            viewOptionService.deleteViewOption(projectId, userId);
        } else {
            throw new NoSuchDataException(PROJECT_NOT_FOUND);
        }
    }

    @Override
    public void favoriteProject(String projectId, String userId) {
        toggleProjectIsFavorite(projectId, userId, NUM_1);
    }

    @Override
    public void unFavoriteProject(String projectId, String userId) {
        toggleProjectIsFavorite(projectId, userId, NUM_O);
    }

    public void toggleProjectIsFavorite(String projectId, String userId, Integer isFavorite) {
        LambdaUpdateWrapper<Project> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Project::getProjectId, projectId);
        updateWrapper.eq(Project::getCreator, userId);

        Project project = new Project();
        project.setUpdatedAt(Instant.now());
        project.setIsFavorite(isFavorite);

        int result = projectMapper.update(project, updateWrapper);
        if (result == 0) {
            throw new NoSuchDataException(PROJECT_NOT_FOUND);
        }
    }

    @Override
    public List<ProjectItem> getProjectList(String userId) {
        LambdaQueryWrapper<Project> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Project::getCreator, userId);
        queryWrapper.eq(Project::getIsInboxProject, NUM_O);
        queryWrapper.eq(Project::getIsArchived, NUM_O);
        List<Project> projects = projectMapper.selectList(queryWrapper);

        List<String> projectIds = projects.stream().map(Project::getProjectId).collect(Collectors.toList());

        // Get view option for each project
        HashMap<String, ViewOption> viewOptionsMap = viewOptionService.getViewOptionsMap(projectIds, userId);

        // Construct project view options
        List<ProjectViewOption> projectViewOptions = new ArrayList<>();
        for (String projectId : projectIds) {
            ProjectViewOption projectViewOption = new ProjectViewOption();
            projectViewOption.setProjectId(projectId);

            ViewOption viewOption = viewOptionsMap.get(projectId);
            if (null != viewOption) {
                projectViewOption.setShowArchived(viewOption.getShowArchived());
                projectViewOption.setShowDeleted(viewOption.getShowDeleted());
            }

            projectViewOptions.add(projectViewOption);
        }

        // Get note count for each project
        HashMap<String, Long> noteCountMap = noteManager.getNoteCountByProjects(projectViewOptions, userId);

        List<ProjectItem> projectItems = new ArrayList<>();
        for (Project project : projects) {
            ProjectItem projectItem = new ProjectItem();
            projectItem.setName(project.getName());
            projectItem.setProjectId(project.getProjectId());
            projectItem.setColor(project.getColor());
            projectItem.setIsFavorite(project.getIsFavorite());
            projectItem.setNoteCount(noteCountMap.getOrDefault(project.getProjectId(), null));
            projectItems.add(projectItem);
        }

        return projectItems;
    }

    @Override
    public List<SelectItem> getProjectSelectItems(ProjectRequest request, String userId) {
        List<Project> projects = projectMapper.getProjects(request, userId);
        if (projects == null || projects.isEmpty()) {
            return new ArrayList<>();
        }

        List<SelectItem> projectSelectItems = new ArrayList<>();
        for (Project project : projects) {
            String projectId = project.getProjectId();
            SelectItem projectSelectItem = new SelectItem();
            projectSelectItem.setName(project.getName());
            projectSelectItem.setValue(projectId);
            projectSelectItem.setColor(project.getColor());
            projectSelectItems.add(projectSelectItem);
        }

        return projectSelectItems;
    }
}

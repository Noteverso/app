package com.noteverso.core.service;

import com.noteverso.common.exceptions.BaseException;
import com.noteverso.common.exceptions.BusinessException;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dto.ProjectItem;
import com.noteverso.core.manager.NoteManager;
import com.noteverso.core.manager.UserConfigManager;
import com.noteverso.core.model.Project;
import com.noteverso.core.model.ViewOption;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.request.ProjectUpdateRequest;
import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.service.impl.ProjectServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    ProjectMapper projectMapper;

    @Mock
    ViewOptionService viewOptionService;

    @Mock
    UserConfigManager userConfigManager;

    @Mock
    NoteMapper noteMapper;

    @Mock
    NoteManager noteManager;

    @Spy
    @InjectMocks
    ProjectServiceImpl projectService;

    @Test
    void should_createProjectSuccessfully_withProjectRequest() {
        // Arrange
        String userId = "test";
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("test");
        request.setColor("red");

        when(projectService.getProjectQuota(userId)).thenReturn(10L);
        when(projectService.getProjectCount(userId)).thenReturn(0L);

        // Act
        projectService.createProject(request, userId);

        // Assert
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectMapper).insert(captor.capture());
        Project project = captor.getValue();
        assertEquals(request.getName(), project.getName());
    }

    @Test
    void createProject_shouldThrowException_whenProjectQuotaIsReached() {
        // Arrange
        String userId = "test";
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("test");
        request.setColor("red");
        when(projectService.getProjectQuota(userId)).thenReturn(5L);
        when(projectService.getProjectCount(userId)).thenReturn(5L);

        // Act
        Executable executable = () -> projectService.createProject(request, userId);
        ThrowableAssert.ThrowingCallable callable = () -> projectService.createProject(request, userId);

        // Assert
        assertThrows(BaseException.class, executable);
        assertThatThrownBy(callable).isInstanceOf(BusinessException.class).hasMessage("The project quota has been reached");
    }

    @Test
    void should_createViewOption_whenCreateProject() {
        String userId = "test";
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("test");
        request.setColor("red");
        when(projectService.getProjectQuota(userId)).thenReturn(10L);
        when(projectService.getProjectCount(userId)).thenReturn(5L);

        // Act
        projectService.createProject(request, userId);

        // Assert
        verify(viewOptionService, times(1)).createViewOption(any(ViewOptionCreate.class), eq(userId));
    }

    @Test
    void should_updateProjectSuccessfully_withProjectUpdateRequest() {
        // Arrange
        ProjectUpdateRequest request = new ProjectUpdateRequest();
        request.setName("test");
        request.setColor("red");
        when(projectMapper.update(any(Project.class), any())).thenReturn(1);

        // Act
        projectService.updateProject("123", request, "123");

        // Assert
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectMapper).update(captor.capture(), any());

        Project project = captor.getValue();
        assertEquals(request.getName(), project.getName());
    }

    @Test
    void should_updateProjectIsArchived_whenArchiveProject() {
        // Arrange
        String projectId = "123";
        String userId = "123";

        when(projectMapper.update(any(Project.class), any())).thenReturn(1);

        // Act
        projectService.updateProjectIsArchived(projectId, userId, 1);

        // Assert
        verify(noteMapper, times(1)).updateNoteIsArchivedByProject(projectId, userId, 1);
    }

    @Test
    void updateProjectIsArchived_shouldThrowNoSuchDataException_whenProjectIsNotFound() {
        // Arrange
        String projectId = "123";
        String userId = "123";

        when(projectMapper.update(any(Project.class), any())).thenReturn(0);

        // Act
        ThrowableAssert.ThrowingCallable callable = () -> projectService.updateProjectIsArchived(projectId, userId, 1);

        // Assert
        assertThatThrownBy(callable).isInstanceOf(NoSuchDataException.class).hasMessage("Project not found");
    }

    @Test
    void should_deleteProjectSuccessfully() {
        // Arrange
        String projectId = "123";
        String userId = "123";
        when(projectMapper.delete(any())).thenReturn(1);

        // Act
        projectService.deleteProject(projectId, userId);

        // Assert
        verify(noteMapper, times(1)).updateNotesIsDeletedByProject(projectId, userId);
    }

    @Test
    void should_updateProjectIsFavorite_whenFavoriteProject() {
        // Arrange
        String projectId = "123";
        String userId = "123";

        when(projectMapper.update(any(Project.class), any())).thenReturn(1);

        // Act
        projectService.toggleProjectIsFavorite(projectId, userId, 1);

        // Assert
        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectMapper).update(captor.capture(), any());

        Project project = captor.getValue();
        assertThat(project.getIsFavorite()).isEqualTo(1);
    }

    @Test
    void should_returnProjectList_whenNoteCountIsNull() {
        // Arrange
        String userId = "123";
        List<String> nonInboxProjectIds = List.of("1", "2", "3", "4", "5");
        List<Project> projects = new ArrayList<>();
        for (String projectId : nonInboxProjectIds) {
            Project project = constructProject("hello World" + projectId, projectId, 0);
            projects.add(project);
        }

        when(projectMapper.selectList(any())).thenReturn(projects);
        when(viewOptionService.getViewOptionsMap(any(), any())).thenReturn(new HashMap<>());
        when(noteManager.getNoteCountByProjects(any(), any())).thenReturn(new HashMap<>());

        // Act
        List<ProjectItem> result = projectService.getProjectList(userId);

        // Assert
        assertThat(result).hasSize(nonInboxProjectIds.size());
        assertThat(result.get(0).getNoteCount()).isNull();
    }

    @Test
    void should_returnProjectList_whenNoteCountIsNotNull() {
        // Arrange
        String userId = "123";
        String projectId1 = "1";
        String projectId2 = "2";
        List<String> nonInboxProjectIds = List.of(projectId1, projectId2);
        List<Project> projects = new ArrayList<>();
        for (String projectId : nonInboxProjectIds) {
            Project project = constructProject("hello World", projectId, 0);
            projects.add(project);
        }

        when(projectMapper.selectList(any())).thenReturn(projects);
        when(viewOptionService.getViewOptionsMap(any(), any())).thenReturn(new HashMap<>());

        HashMap<String, Long> noteCountMap = new HashMap<>();
        noteCountMap.put(projectId1, 10L);
        noteCountMap.put(projectId2, 20L);
        when(noteManager.getNoteCountByProjects(any(), any())).thenReturn(noteCountMap);

        // Act
        List<ProjectItem> result = projectService.getProjectList(userId);

        // Assert
        assertThat(result).hasSize(nonInboxProjectIds.size());
        assertThat(result.get(0).getNoteCount()).isEqualTo(10);
    }

    private Project constructProject(String name, String projectId, Integer isInboxProject) {
        Project project = new Project();
        project.setName(name);
        project.setProjectId(projectId);
        project.setIsFavorite(0);
        project.setIsArchived(0);
        project.setIsCollapsed(0);
        project.setIsInboxProject(isInboxProject);
        project.setIsShared(0);
        project.setColor("red");
        project.setCreator("1");
        project.setUpdater("1");
        return project;
    }

    private ProjectItem constructProjectItem(Project project) {
        ProjectItem projectItem = new ProjectItem();
        projectItem.setName(project.getName());
        projectItem.setProjectId(project.getProjectId());
        projectItem.setColor(project.getColor());
        projectItem.setIsFavorite(project.getIsFavorite());
        projectItem.setNoteCount(20L);
        return projectItem;
    }

    private ViewOption constructViewOption(String objectId, String userId) {
        ViewOption viewOption = new ViewOption();
        viewOption.setObjectId(objectId);
        viewOption.setShowArchived(0);
        viewOption.setShowDeleted(0);
        viewOption.setCreator(userId);
        viewOption.setUpdater(userId);
        return viewOption;
    }

}
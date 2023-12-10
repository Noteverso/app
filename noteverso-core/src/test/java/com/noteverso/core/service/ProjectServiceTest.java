package com.noteverso.core.service;

import com.noteverso.common.exceptions.BaseException;
import com.noteverso.common.exceptions.BusinessException;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.manager.UserConfigManager;
import com.noteverso.core.model.Project;
import com.noteverso.core.request.ProjectCreateRequest;
import com.noteverso.core.request.ViewOptionCreate;
import com.noteverso.core.service.impl.ProjectServiceImpl;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

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
        request.setChildOrder(1);

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
    void should_throwException_whenProjectQuotaIsReached() {
        // Arrange
        String userId = "test";
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setName("test");
        request.setColor("red");
        request.setChildOrder(1);
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
        request.setChildOrder(1);
        when(projectService.getProjectQuota(userId)).thenReturn(10L);
        when(projectService.getProjectCount(userId)).thenReturn(5L);

        // Act
        projectService.createProject(request, userId);

        // Assert
        verify(viewOptionService, times(1)).createViewOption(any(ViewOptionCreate.class), eq(userId));
    }
}
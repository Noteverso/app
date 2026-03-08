package com.noteverso.core.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dao.UserConfigMapper;
import com.noteverso.core.dao.UserMapper;
import com.noteverso.core.model.entity.Project;
import com.noteverso.core.model.entity.User;
import com.noteverso.core.model.entity.UserConfig;
import com.noteverso.core.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private UserConfigMapper userConfigMapper;

    @Mock
    private ViewOptionService viewOptionService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void should_createUser_successfully() {
        // Arrange
        String email = "test@test.com";
        String username = "testuser";
        String password = "password123";
        
        Project inboxProject = new Project();
        inboxProject.setProjectId("project1");
        
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(projectService.constructInboxProject(anyString())).thenReturn(inboxProject);
        when(userMapper.insert(any(User.class))).thenReturn(1);
        when(projectMapper.insert(any(Project.class))).thenReturn(1);
        when(userConfigMapper.insert(any(UserConfig.class))).thenReturn(1);

        // Act
        userService.createUser(email, username, password);

        // Assert
        verify(userMapper).insert(any(User.class));
        verify(projectMapper).insert(any(Project.class));
        verify(userConfigMapper).insert(any(UserConfig.class));
        verify(viewOptionService).createViewOption(any(), anyString());
    }

    @Test
    void should_existsByEmail_returnTrue_whenEmailExists() {
        // Arrange
        String email = "existing@test.com";
        when(userMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(true);

        // Act
        boolean exists = userService.existsByEmail(email);

        // Assert
        assertThat(exists).isTrue();
        verify(userMapper).exists(any(LambdaQueryWrapper.class));
    }

    @Test
    void should_existsByEmail_returnFalse_whenEmailNotExists() {
        // Arrange
        String email = "nonexistent@test.com";
        when(userMapper.exists(any(LambdaQueryWrapper.class))).thenReturn(false);

        // Act
        boolean exists = userService.existsByEmail(email);

        // Assert
        assertThat(exists).isFalse();
        verify(userMapper).exists(any(LambdaQueryWrapper.class));
    }
}
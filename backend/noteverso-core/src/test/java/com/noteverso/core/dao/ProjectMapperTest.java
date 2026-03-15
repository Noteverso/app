package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.entity.Project;
import com.noteverso.core.model.request.ProjectRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisPlusTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProjectMapperTest {
    @Autowired
    ProjectMapper projectMapper;

    @Test
    void should_findProject_whenSelectByProjectId() {
        // Arrange
        String projectId = "123456789";
        String userId = "123456789";
        Project project = constructProject(projectId, userId, "java");

        projectMapper.insert(project);

        // Act
        Project findedProject = projectMapper.selectByProjectId(projectId, userId);

        // Assert
        assertThat(findedProject.getProjectId()).isEqualTo(projectId);
    }

    @Test
    void should_returnProjects_whenGetProjects() {
        // Arrange
        List<String> projectIds = Arrays.asList("111222", "111333", "111444");
        String userId = "1";
        for (String projectId : projectIds) {
            Project project = constructProject(projectId, userId, projectId);
            projectMapper.insert(project);
        }

        // Act
        ProjectRequest request = new ProjectRequest();
        request.setProjectIds(new HashSet<>(projectIds));
        List<Project> projects = projectMapper.getProjects(request, userId);

        // Assert
        assertThat(projects.size()).isEqualTo(3);
    }

    @Test
    void should_getProjects_returnEmpty_whenEmptyProjectIds() {
        // Arrange
        String userId = "1";
        ProjectRequest request = new ProjectRequest();
        request.setProjectIds(new HashSet<>());

        // Act
        List<Project> projects = projectMapper.getProjects(request, userId);

        // Assert
        assertThat(projects).isEmpty();
    }

    @Test
    void should_returnNull_whenProjectNotFound() {
        // Arrange
        String projectId = "nonexistent";
        String userId = "user1";

        // Act
        Project project = projectMapper.selectByProjectId(projectId, userId);

        // Assert
        assertThat(project).isNull();
    }

    @Test
    void should_returnNull_whenWrongUserId() {
        // Arrange
        String projectId = "project123";
        String userId = "user1";
        String wrongUserId = "user2";
        
        Project project = constructProject(projectId, userId, "Test Project");
        projectMapper.insert(project);

        // Act
        Project result = projectMapper.selectByProjectId(projectId, wrongUserId);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void should_getProjects_returnEmpty_whenNullProjectIds() {
        // Arrange
        String userId = "user1";
        ProjectRequest request = new ProjectRequest();
        request.setProjectIds(null);

        // Act
        List<Project> projects = projectMapper.getProjects(request, userId);

        // Assert
        assertThat(projects).isEmpty();
    }

    @Test
    void should_getProjects_filterByName() {
        // Arrange
        String userId = "user1";
        Project project1 = constructProject("p1", userId, "Java Project");
        Project project2 = constructProject("p2", userId, "Python Project");
        Project project3 = constructProject("p3", userId, "JavaScript Project");
        
        projectMapper.insert(project1);
        projectMapper.insert(project2);
        projectMapper.insert(project3);

        // Act - LIKE search matches both "Java Project" and "JavaScript Project"
        ProjectRequest request = new ProjectRequest();
        request.setName("Java");
        List<Project> projects = projectMapper.getProjects(request, userId);

        // Assert
        assertThat(projects).hasSize(2);
        assertThat(projects).allMatch(p -> p.getName().toLowerCase().contains("java"));
    }

    @Test
    void should_getProjects_returnEmpty_whenNoMatch() {
        // Arrange
        String userId = "user1";
        Project project = constructProject("p1", userId, "Test Project");
        projectMapper.insert(project);

        // Act
        ProjectRequest request = new ProjectRequest();
        request.setName("NonExistent");
        List<Project> projects = projectMapper.getProjects(request, userId);

        // Assert
        assertThat(projects).isEmpty();
    }

    private Project constructProject(String projectId, String userId, String name) {
        Project project = new Project();
        project.setProjectId(projectId);
        project.setName(name);
        project.setColor("RED");
        project.setChildOrder(1);
        project.setCreator(userId);
        project.setUpdater(userId);
        return project;
    }

}

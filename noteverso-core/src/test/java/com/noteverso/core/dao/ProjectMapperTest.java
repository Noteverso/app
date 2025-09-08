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

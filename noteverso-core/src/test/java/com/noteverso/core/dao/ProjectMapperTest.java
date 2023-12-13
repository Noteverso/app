package com.noteverso.core.dao;

import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
import com.noteverso.core.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

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
        Project project = new Project();
        project.setProjectId(projectId);
        project.setName("JAVA");
        project.setColor("RED");
        project.setChildOrder(1);
        project.setCreator(userId);
        project.setUpdater(userId);

        projectMapper.insert(project);

        // Act
        Project findedProject = projectMapper.selectByProjectId(projectId, userId);

        // Assert
        assertThat(findedProject.getProjectId()).isEqualTo(projectId);
    }



}

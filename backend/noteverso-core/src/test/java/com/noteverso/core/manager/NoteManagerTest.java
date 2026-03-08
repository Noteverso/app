package com.noteverso.core.manager;

import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.manager.impl.NoteManagerImpl;
import com.noteverso.core.model.dto.ProjectViewOption;
import com.noteverso.core.service.RelationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class NoteManagerTest {

    @Mock
    private NoteMapper noteMapper;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private RelationService relationService;

    @InjectMocks
    private NoteManagerImpl noteManager;

    @Test
    void should_getNoteCountByProjects_returnEmpty_whenEmptyList() {
        // Arrange
        String userId = "user1";
        List<ProjectViewOption> emptyList = List.of();

        // Act
        HashMap<String, Long> result = noteManager.getNoteCountByProjects(emptyList, userId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void should_getNoteCountByProjects_returnEmpty_whenNull() {
        // Arrange
        String userId = "user1";

        // Act
        HashMap<String, Long> result = noteManager.getNoteCountByProjects(null, userId);

        // Assert
        assertThat(result).isEmpty();
    }
}

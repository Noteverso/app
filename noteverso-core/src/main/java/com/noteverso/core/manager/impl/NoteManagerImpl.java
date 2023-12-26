package com.noteverso.core.manager.impl;

import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dto.NoteCount;
import com.noteverso.core.dto.ProjectViewOption;
import com.noteverso.core.manager.NoteManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@AllArgsConstructor
public class NoteManagerImpl implements NoteManager {
    private final NoteMapper noteMapper;
    @Override
    public HashMap<String, Long> getNoteCountByProjects(List<ProjectViewOption> projectViewOptions, String userId) {
        HashMap<String, Long> result = new HashMap<>();
        if (!projectViewOptions.isEmpty()) {
            List<NoteCount> noteCounts = noteMapper.getNoteCountByProjects(projectViewOptions, userId);
            for (NoteCount noteCount : noteCounts) {
                result.put(noteCount.getProjectId(), noteCount.getNoteCount());
            }
        }

        return result;
    }
}

package com.noteverso.core.manager;

import com.noteverso.core.dto.ProjectViewOption;

import java.util.HashMap;
import java.util.List;

public interface NoteManager {
    HashMap<String, Long> getNoteCountByProjects(List<ProjectViewOption> projectViewOptions, String userId);
}

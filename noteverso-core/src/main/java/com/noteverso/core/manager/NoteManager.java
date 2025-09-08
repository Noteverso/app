package com.noteverso.core.manager;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noteverso.core.model.dto.LabelItem;
import com.noteverso.core.model.dto.NoteItem;
import com.noteverso.core.model.dto.ProjectViewOption;
import com.noteverso.core.model.entity.Note;
import com.noteverso.core.model.entity.Project;
import com.noteverso.core.model.entity.ViewOption;
import com.noteverso.core.model.pagination.PageResult;

import java.util.HashMap;
import java.util.List;

public interface NoteManager {
    HashMap<String, Long> getNoteCountByProjects(List<ProjectViewOption> projectViewOptions, String userId);

    PageResult<NoteItem> getNoteItemPage(Page<Note> notePage, ViewOption viewOption, String userId);

    public NoteItem constructNoteItem(
            Note note,
            HashMap<String, Long> attachmentCountMap,
            HashMap<String, Long> referencingCountMap,
            HashMap<String, Long> referencedCountMap,
            HashMap<String, List<LabelItem>> noteLabelMap,
            Project project);
}

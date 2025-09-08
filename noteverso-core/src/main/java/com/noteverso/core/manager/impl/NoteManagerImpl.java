package com.noteverso.core.manager.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.model.dto.*;
import com.noteverso.core.manager.NoteManager;
import com.noteverso.core.model.entity.Note;
import com.noteverso.core.model.entity.Project;
import com.noteverso.core.model.entity.ViewOption;
import com.noteverso.core.model.pagination.PageResult;
import com.noteverso.core.model.request.ProjectRequest;
import com.noteverso.core.service.RelationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.noteverso.common.constant.NumConstants.NUM_1;

@Service
@AllArgsConstructor
public class NoteManagerImpl implements NoteManager {
    private final NoteMapper noteMapper;
    private final ProjectMapper projectMapper;
    private final RelationService relationService;

    @Override
    public HashMap<String, Long> getNoteCountByProjects(List<ProjectViewOption> projectViewOptions, String userId) {
        HashMap<String, Long> result = new HashMap<>();
        if (projectViewOptions != null && !projectViewOptions.isEmpty()) {
            List<NoteCountForProject> noteCounts = noteMapper.getNoteCountByProjects(projectViewOptions, userId);
            for (NoteCountForProject noteCount : noteCounts) {
                result.put(noteCount.getProjectId(), noteCount.getNoteCount());
            }
        }

        return result;
    }

    @Override
    public PageResult<NoteItem> getNoteItemPage(Page<Note> notePage, ViewOption viewOption, String userId) {
        if (null == notePage || notePage.getRecords().isEmpty()) {
            return new PageResult<>();
        }

        List<Note> notes = notePage.getRecords();

        Map<String, Project> projectMap = new HashMap<>();
        Set<String> projectIds = notes.stream().map(Note::getProjectId).collect(Collectors.toSet());

        // get projects info
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setProjectIds(projectIds);
        List<Project> projects = projectMapper.getProjects(projectRequest, userId);
        for (Project project : projects) {
            projectMap.put(project.getProjectId(), project);
        }

        List<String> noteIds = notes.stream().map(Note::getNoteId).toList();

        // get referencing and referenced count for each note
        HashMap<String, Long> referencingCountMap = new HashMap<>();
        HashMap<String, Long> referencedCountMap = new HashMap<>();
        if (viewOption != null && Objects.equals(viewOption.getShowRelationNoteCount(), NUM_1)) {
            referencedCountMap = relationService.getReferencedCountByReferencedNoteIds(noteIds, userId);
            referencingCountMap = relationService.getReferencingCountByReferencingNoteIds(noteIds, userId);
        }

        // get attachment count for each note
        HashMap<String, Long> attachmentCountMap = new HashMap<>();
        if (viewOption != null && Objects.equals(viewOption.getShowAttachmentCount(), NUM_1)) {
            attachmentCountMap = relationService.getAttachmentCountByObjectIds(noteIds, userId);
        }

        // get label ids for each note
        HashMap<String, List<LabelItem>> labelMap = relationService.getLabelsByNoteIds(noteIds, userId);
        List<NoteItem> responseList = new ArrayList<>();
        for (Note note : notes) {
            NoteItem response = constructNoteItem(note, attachmentCountMap, referencingCountMap, referencedCountMap, labelMap, projectMap.get(note.getProjectId()));
            responseList.add(response);
        }

        PageResult<NoteItem> responsePage = new PageResult<>();
        responsePage.setRecords(responseList);
        responsePage.setTotal(notePage.getTotal());
        responsePage.setPageIndex(notePage.getCurrent());
        responsePage.setPageSize(notePage.getSize());
        return responsePage;
    }

    @Override
    public NoteItem constructNoteItem(
            Note note,
            HashMap<String, Long> attachmentCountMap,
            HashMap<String, Long> referencingCountMap,
            HashMap<String, Long> referencedCountMap,
            HashMap<String, List<LabelItem>> noteLabelMap,
            Project project) {
        NoteItem noteItem = new NoteItem();
        String noteId = note.getNoteId();
        noteItem.setContent(note.getContent());
        noteItem.setNoteId(noteId);
        noteItem.setIsPinned(note.getIsPinned());
        noteItem.setIsArchived(note.getIsArchived());
        noteItem.setIsDeleted(note.getIsDeleted());
        noteItem.setCreator(note.getCreator());
        noteItem.setLabels(noteLabelMap.get(noteId) != null ? noteLabelMap.get(noteId) : null);
        noteItem.setAddedAt(note.getAddedAt() != null ? note.getAddedAt().toString() : null);
        noteItem.setUpdatedAt(note.getUpdatedAt() != null ? note.getUpdatedAt().toString() : null);
        noteItem.setAttachmentCount(attachmentCountMap.get(noteId) != null ? attachmentCountMap.get(noteId) : null);
        noteItem.setReferencingCount(referencingCountMap.get(noteId) != null ? referencingCountMap.get(noteId) : null);
        noteItem.setReferencedCount(referencedCountMap.get(noteId) != null ? referencedCountMap.get(noteId) : null);
        ProjectItem projectItem = new ProjectItem();
        projectItem.setProjectId(project.getProjectId());
        projectItem.setName(project.getName());
        noteItem.setProject(projectItem);
        return noteItem;
    }
}

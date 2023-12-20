package com.noteverso.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.noteverso.common.exceptions.NoSuchDataException;
import com.noteverso.common.util.IPUtils;
import com.noteverso.common.util.SnowFlakeUtils;
import com.noteverso.core.dao.NoteMapper;
import com.noteverso.core.dao.ProjectMapper;
import com.noteverso.core.dto.NoteDTO;
import com.noteverso.core.manager.UserConfigManager;
import com.noteverso.core.model.Note;
import com.noteverso.core.model.Project;
import com.noteverso.core.model.UserConfig;
import com.noteverso.core.request.NoteCreateRequest;
import com.noteverso.core.request.NoteUpdateRequest;
import com.noteverso.core.service.RelationService;
import lombok.AllArgsConstructor;
import com.noteverso.core.service.NoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.noteverso.common.constant.NumConstants.*;
import static com.noteverso.core.constant.ExceptionConstants.NOTE_NOT_FOUND;
import static com.noteverso.core.constant.ExceptionConstants.PROJECT_NOT_FOUND;

@Service
@AllArgsConstructor
@Slf4j
public class NoteServiceImpl implements NoteService {
    private final NoteMapper noteMapper;
    private final RelationService relationService;
    private final ProjectMapper projectMapper;
    private final UserConfigManager userConfigManager;

    private final SnowFlakeUtils snowFlakeUtils = new SnowFlakeUtils(
            NOTE_DATACENTER_ID, IPUtils.getHostAddressWithLong() % NUM_31
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createNote(NoteCreateRequest request, String userId) {
        String projectId = request.getProjectId();
        String noteId = String.valueOf(snowFlakeUtils.nextId());

        // Create a note
        Note note = constructNote(noteId, projectId, request.getContent(), userId);
        noteMapper.insert(note);

        // create the relation with labels
        relationService.insertNoteLabelRelation(request.getLabels(), noteId, userId);

        // create the relation with the other notes
        relationService.insertNoteRelation(request.getLinkedNotes(), noteId, userId);

        //  create the relation with attachments
        relationService.insertNoteAttachmentRelation(request.getFiles(), noteId, userId);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateNote(String noteId, String userId, NoteUpdateRequest request) {
        LambdaUpdateWrapper<Note> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Note::getNoteId, noteId);
        wrapper.set(Note::getContent, request.getContent());
        wrapper.set(Note::getUpdatedAt, Instant.now());

        if (null != request.getProjectId()) {
            wrapper.set(Note::getProjectId, request.getProjectId());
        }

        noteMapper.update(null, wrapper);

        // update the relation with labels

        relationService.updateNoteLabelRelation(request.getLabels(), noteId, userId);
        // update the relation with the other notes
        relationService.updateNoteRelation(request.getLinkedNotes(), noteId, userId);

        // update the relation with the attachments
        relationService.updateNoteAttachment(request.getFiles(), noteId, userId);
    }

    public Note constructNote(String noteId, String projectId, String content, String userId) {
        return Note.builder()
                .noteId(noteId)
                .content(content)
                .projectId(projectId)
                .creator(userId)
                .updater(userId)
                .addedAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Override
    public void restoreNote(String id, String userId) {
        LambdaUpdateWrapper<Note> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Note::getNoteId, id);
        updateWrapper.eq(Note::getIsDeleted, NUM_1);
        Note note = noteMapper.selectOne(updateWrapper);

        if (null == note) {
            throw new NoSuchDataException(NOTE_NOT_FOUND);
        }

        Note newNote = new Note();
        newNote.setIsDeleted(NUM_O);
        newNote.setUpdatedAt(Instant.now());
        if (note.getProjectId() == null) {
            UserConfig userConfig = userConfigManager.getUserConfig(userId);
            if (userConfig == null || userConfig.getInboxProjectId() == null) {
                log.error("User config: {}", userConfig);
                throw new NoSuchDataException(PROJECT_NOT_FOUND);
            }
            newNote.setProjectId(note.getProjectId());
        }

        noteMapper.update(newNote, updateWrapper);
    }

    @Override
    public void moveNoteToTrash(String id) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);
        noteWrapper.set(Note::getIsDeleted, NUM_1);
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);
    }

    @Override
    public void toggleArchive(String id, Boolean toggle) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);
        if (toggle) {
            noteWrapper.set(Note::getIsArchived, NUM_1);
        } else {
            noteWrapper.set(Note::getIsArchived, NUM_O);
        }
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);

    };

    @Override
    public void toggleFavorite(String id, Boolean toggle) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);
        if (toggle) {
            noteWrapper.set(Note::getIsFavorite, NUM_1);
        } else {
            noteWrapper.set(Note::getIsFavorite, NUM_O);
        }
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);
    };

    @Override
    public void togglePin(String id, Boolean toggle) {
        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);
        if (toggle) {
            noteWrapper.set(Note::getIsPin, NUM_1);
        } else {
            noteWrapper.set(Note::getIsPin, NUM_O);
        }
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);
    }

    @Override
    public void moveNote(String id, String projectId, String userId) {
        Project project = projectMapper.selectByProjectId(projectId, userId);
        if (null == project) {
            throw new NoSuchDataException(PROJECT_NOT_FOUND);
        }

        LambdaUpdateWrapper<Note> noteWrapper = new LambdaUpdateWrapper<>();
        noteWrapper.eq(Note::getNoteId, id);
        noteWrapper.eq(Note::getIsDeleted, NUM_O);

        noteWrapper.set(Note::getProjectId, projectId);
        noteWrapper.set(Note::getUpdatedAt, Instant.now());
        noteMapper.update(null, noteWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteNote(String id, String userId) {
        LambdaUpdateWrapper<Note> qw = new LambdaUpdateWrapper<>();
        qw.eq(Note::getNoteId, id);
        qw.eq(Note::getCreator, userId);
        qw.eq(Note::getIsDeleted, NUM_1);

        int result = noteMapper.delete(qw);
        if (result > 0) {
            log.info("Delete note successfully: {}", id);
            relationService.deleteNoteRelation(id, userId);
            relationService.deleteNoteLabelRelation(id, userId);
            relationService.deleteNoteAttachmentRelation(id, userId);
        } else {
            throw new NoSuchDataException(NOTE_NOT_FOUND);
        }
    }


    @Override
    public NoteDTO getNoteDetail(String noteId, String userId) {
        // TODO
        return null;
    }
}

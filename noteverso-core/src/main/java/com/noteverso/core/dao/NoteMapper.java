package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.Note;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoteMapper extends BaseMapper<Note> {
    Note selectByNoteId(String noteId);
}

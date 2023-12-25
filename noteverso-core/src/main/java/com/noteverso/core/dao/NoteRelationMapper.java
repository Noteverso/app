package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.dto.ReferencedNoteCount;
import com.noteverso.core.dto.ReferencingNoteCount;
import com.noteverso.core.model.NoteRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteRelationMapper extends BaseMapper<NoteRelation> {
    void batchInsert(@Param("noteRelations") List<NoteRelation> noteRelations);

    List<ReferencingNoteCount> getReferencingNoteCountByReferencingIds(List<String> referencingNoteIds, String userId);

    List<ReferencedNoteCount> getReferencedNoteCountByReferencedIds(List<String> referencedNoteIds, String userId);
}

package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.NoteRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NoteRelationMapper extends BaseMapper<NoteRelation> {
    void batchInsert(@Param("noteRelations") List<NoteRelation> noteRelations);
}

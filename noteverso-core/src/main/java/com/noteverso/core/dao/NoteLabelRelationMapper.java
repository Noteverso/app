package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.NoteLabelRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface NoteLabelRelationMapper extends BaseMapper<NoteLabelRelation> {
    void batchInsert(@Param("noteLabelRelations") List<NoteLabelRelation> noteLabelRelations);
}

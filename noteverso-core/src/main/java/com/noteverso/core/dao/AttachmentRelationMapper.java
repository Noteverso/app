package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.AttachmentRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttachmentRelationMapper extends BaseMapper<AttachmentRelation> {
    void batchInsert(@Param("attachmentRelations") List<AttachmentRelation> attachmentRelations);
}

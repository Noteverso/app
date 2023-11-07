package com.noteverso.attachment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.attachment.dto.AttachmentDTO;
import com.noteverso.attachment.model.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment> {
    void batchInsert(@Param("files") List<AttachmentDTO> files);
}

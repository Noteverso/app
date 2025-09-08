package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.entity.Label;
import com.noteverso.core.model.request.LabelRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LabelMapper extends BaseMapper<Label> {
    List<Label> getLabels(LabelRequest request, String userId);
}

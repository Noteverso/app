package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.entity.ViewOption;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ViewOptionMapper extends BaseMapper<ViewOption> {
    List<ViewOption> batchSelectByObjectIds(List<String> objectIds, String userId);
}

package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}

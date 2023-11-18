package com.noteverso.project.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.project.model.Project;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
}

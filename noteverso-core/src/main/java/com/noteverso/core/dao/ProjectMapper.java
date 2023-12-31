package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.Project;
import com.noteverso.core.request.ProjectRequest;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProjectMapper extends BaseMapper<Project> {
    Project selectByProjectId(String projectId, String userId);

    List<Project> getProjects(ProjectRequest request, String userId);
}

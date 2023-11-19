package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.UserConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserConfigMapper extends BaseMapper<UserConfig> {
}

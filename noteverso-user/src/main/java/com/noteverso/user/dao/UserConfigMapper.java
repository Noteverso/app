package com.noteverso.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.user.model.UserConfig;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserConfigMapper extends BaseMapper<UserConfig> {
}

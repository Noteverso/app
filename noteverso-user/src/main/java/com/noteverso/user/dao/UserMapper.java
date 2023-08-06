package com.noteverso.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.user.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    User findUserByUsername(String username);

    User findUserByEmail(String email);
}

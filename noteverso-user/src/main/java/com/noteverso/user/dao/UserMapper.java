package com.noteverso.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    User findUserByUsername(@Param("username") String username);

    User findUserByEmail(String email);
}

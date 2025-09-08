package com.noteverso.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.noteverso.core.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    Optional<User> findUserByUsername(@Param("username") String username);

    User findUserByEmail(String email);
}

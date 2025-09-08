package com.noteverso.core.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.noteverso.core.dao.UserConfigMapper;
import com.noteverso.core.model.dto.UserDTO;
import com.noteverso.core.manager.UserConfigManager;
import com.noteverso.core.model.entity.User;
import com.noteverso.core.model.entity.UserConfig;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserConfigManagerImpl implements UserConfigManager {
    private final UserConfigMapper userConfigMapper;

    @Override
    public UserDTO constructUserDTO(User user, UserConfig userConfig) {
        var userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setUsername(user.getUsername());
        userDTO.setAuthority(user.getAuthority());
        userDTO.setHasPassword(user.getHasPassword());
        userDTO.setIsPremium(user.getIsPremium());
        userDTO.setPremiumStatus(user.getPremiumStatus());
        userDTO.setAuthority(user.getAuthority());
        userDTO.setPremiumUntil(user.getPremiumUntil());
        userDTO.setJoinedAt(user.getJoinedAt());
        userDTO.setInboxProjectId(userConfig.getInboxProjectId());
        userDTO.setStartPage(userConfig.getStartPage());
        userDTO.setMaxFileSize(userConfig.getMaxFileSize());
        userDTO.setProjectsQuota(userConfig.getProjectsQuota());
        userDTO.setFilesSizeQuota(userConfig.getFilesSizeQuota());
        userDTO.setLinkedNotesQuota(userConfig.getLinkedNotesQuota());
        return userDTO;
    }

    @Override
    public  UserConfig getUserConfig(String userId) {
        LambdaQueryWrapper<UserConfig> qw = new LambdaQueryWrapper<>();
        qw.eq(UserConfig::getUserId, userId);
        return userConfigMapper.selectOne(qw);
    }
}

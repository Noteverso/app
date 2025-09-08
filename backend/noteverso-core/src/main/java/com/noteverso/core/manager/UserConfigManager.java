package com.noteverso.core.manager;

import com.noteverso.core.model.dto.UserDTO;
import com.noteverso.core.model.entity.User;
import com.noteverso.core.model.entity.UserConfig;

public interface UserConfigManager {
    UserDTO constructUserDTO(User user, UserConfig userConfig);

    UserConfig getUserConfig(String userId);
}

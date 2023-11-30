package com.noteverso.core.manager;

import com.noteverso.core.dto.UserDTO;
import com.noteverso.core.model.User;
import com.noteverso.core.model.UserConfig;

public interface UserConfigManager {
    UserDTO constructUserDTO(User user, UserConfig userConfig);

    UserConfig getUserConfig(String userId);
}

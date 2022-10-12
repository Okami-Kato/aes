package com.okami.server.service;

import com.okami.server.service.dto.UserDto;

public interface UserService {

    UserDto getByUsername(String username);

    void register(String username, String password);
}

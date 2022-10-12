package com.okami.server.service.mapper.impl;

import com.okami.server.domain.User;
import com.okami.server.service.dto.UserDto;
import com.okami.server.service.mapper.Mapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDto> {
    @Override
    public UserDto toDto(User entity) {
        return new UserDto(entity.getId(), entity.getUsername());
    }

    @Override
    public User toEntity(UserDto dto) {
        throw new UnsupportedOperationException();
    }
}

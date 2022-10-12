package com.okami.server.service.impl;

import com.okami.server.domain.User;
import com.okami.server.repository.UserRepository;
import com.okami.server.service.UserService;
import com.okami.server.service.dto.UserDto;
import com.okami.server.service.exception.NotFoundException;
import com.okami.server.service.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final Mapper<User, UserDto> userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username \"%s\" not found".formatted(username)));
    }

    @Override
    public UserDto getByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(userMapper::toDto)
            .orElseThrow(() -> new NotFoundException("User with username \"%s\" not found".formatted(username)));
    }

    @Override
    public void register(String username, String password) {
        User user = new User()
                .setUsername(username)
                .setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}

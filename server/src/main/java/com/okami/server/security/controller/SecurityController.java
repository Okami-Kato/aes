package com.okami.server.security.controller;

import com.okami.server.security.dto.SessionKey;
import com.okami.server.security.dto.SessionKeyRequestDto;
import com.okami.server.security.service.SecurityService;
import com.okami.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    private final UserService userService;

    private final SecurityService securityService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestParam String username, @RequestParam String password) {
        userService.register(username, password);
    }

    @PostMapping("/session-key")
    @ResponseStatus(HttpStatus.OK)
    public SessionKey getSessionKey(@RequestBody SessionKeyRequestDto dto, Principal principal) {
        return securityService.getSessionKey(dto.publicKey(), principal.getName());
    }
}

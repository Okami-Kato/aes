package com.okami.server.security.service;

import com.okami.server.security.dto.SessionKey;

public interface SecurityService {

    SessionKey getSessionKey(byte[] publicKey, String username);

    String encryptText(String plainText, String username);

    String decryptText(String encryptedText, String username);
}

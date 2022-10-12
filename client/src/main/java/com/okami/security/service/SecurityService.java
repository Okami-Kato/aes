package com.okami.security.service;

import com.okami.domain.SessionKey;
import com.okami.security.exception.SessionKeyException;

import java.time.Instant;

public interface SecurityService {

    String decryptText(String text) throws SessionKeyException;

    String encryptText(String text) throws SessionKeyException;

    Instant obtainSessionKey();
}

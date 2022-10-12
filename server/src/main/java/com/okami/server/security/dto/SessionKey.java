package com.okami.server.security.dto;

import java.time.Instant;

public record SessionKey(byte[] encryptedAesKey, byte[] encryptedIv, Instant expirationTimestamp) {
}

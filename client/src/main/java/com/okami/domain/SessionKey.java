package com.okami.domain;

import java.time.Instant;

public record SessionKey(byte[] encryptedAesKey, byte[] encryptedIv, Instant expirationTimestamp) {
}

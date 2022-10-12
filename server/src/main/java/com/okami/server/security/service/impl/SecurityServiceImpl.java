package com.okami.server.security.service.impl;

import com.okami.server.security.domain.UserKey;
import com.okami.server.security.dto.SessionKey;
import com.okami.server.security.repository.UserKeyRepository;
import com.okami.server.security.service.SecurityService;
import com.okami.server.service.UserService;
import com.okami.server.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final KeyFactory keyFactoryRSA;

    private final UserKeyRepository userKeyRepository;

    private final UserService userService;

    private final KeyGenerator keyGeneratorAES;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @SneakyThrows
    public SessionKey getSessionKey(byte[] publicKey, String username) {
        Integer userId = userService.getByUsername(username).id();

        EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(publicKey);
        PublicKey key = keyFactoryRSA.generatePublic(encodedKeySpec);

        SecretKey sessionKey = keyGeneratorAES.generateKey();

        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedSessionKey = cipher.doFinal(sessionKey.getEncoded());

        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        byte[] encryptedIv = cipher.doFinal(iv);

        final Instant expirationTimestamp = Instant.now().plus(Duration.ofMinutes(5));

        userKeyRepository.save(
            new UserKey()
                .setUserId(userId)
                .setKey(sessionKey.getEncoded())
                .setIv(iv)
                .setExpirationTimestamp(expirationTimestamp)
        );

        return new SessionKey(encryptedSessionKey, encryptedIv, expirationTimestamp);
    }

    @SneakyThrows
    public String encryptText(String plainText, String username) {
        Integer userId = userService.getByUsername(username).id();
        UserKey userKey = userKeyRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key not found for user \"%s\"".formatted(username)));
        if (Instant.now().isAfter(userKey.getExpirationTimestamp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session key is expired! Please, obtain new session key.");
        }
        final Cipher cipherAESEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(userKey.getKey(), "AES");
        cipherAESEncryption.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(userKey.getIv()));

        return Base64.getEncoder().encodeToString(cipherAESEncryption.doFinal(plainText.getBytes()));
    }

    @SneakyThrows
    public String decryptText(String encryptedText, String username) {
        Integer userId = userService.getByUsername(username).id();
        UserKey userKey = userKeyRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Key not found for user \"%s\"".formatted(username)));
        if (Instant.now().isAfter(userKey.getExpirationTimestamp())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Session key is expired! Please, obtain new session key.");
        }
        final Cipher cipherAESDecryption = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKey secretKey = new SecretKeySpec(userKey.getKey(), "AES");
        cipherAESDecryption.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(userKey.getIv()));

        return new String(cipherAESDecryption.doFinal(Base64.getDecoder().decode(encryptedText)));
    }
}

package com.okami.security.service.impl;

import com.okami.client.SecurityClient;
import com.okami.domain.SessionKey;
import com.okami.security.exception.SessionKeyException;
import com.okami.security.ks.InMemoryKeyStore;
import com.okami.security.service.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SecurityServiceImpl implements SecurityService {

    private final InMemoryKeyStore keyStore;

    private final SecurityClient securityClient;

    @Override
    public String encryptText(String text) throws SessionKeyException {
        try {
            byte[] encrypted = keyStore.getCipherAESEncryption()
                .doFinal(text.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (IllegalStateException e) {
            throw new SessionKeyException("No session key found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String decryptText(String text) throws SessionKeyException {
        try {
            byte[] decrypted = keyStore.getCipherAESDecryption()
                .doFinal(Base64.getDecoder().decode(text.getBytes()));
            return new String(decrypted);
        } catch (IllegalStateException e) {
            throw new SessionKeyException("No session key found");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SneakyThrows
    public Instant obtainSessionKey() {
        SessionKey sessionKey = securityClient.getSessionKey(keyStore.getPublicKey().getEncoded());

        byte[] aesKey = keyStore.getCipherRSA().doFinal(sessionKey.encryptedAesKey());
        byte[] iv = keyStore.getCipherRSA().doFinal(sessionKey.encryptedIv());

        SecretKeySpec secretKeySpec = new SecretKeySpec(aesKey, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        keyStore.getCipherAESDecryption().init(Cipher.DECRYPT_MODE, secretKeySpec,
            ivParameterSpec);
        keyStore.getCipherAESEncryption().init(Cipher.ENCRYPT_MODE, secretKeySpec,
            ivParameterSpec);
        return sessionKey.expirationTimestamp();
    }
}

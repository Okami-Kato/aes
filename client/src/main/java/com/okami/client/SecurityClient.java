package com.okami.client;

import com.okami.client.dto.SessionKeyRequestDto;
import com.okami.domain.SessionKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SecurityClient {

    @Value("${note-server.url}")
    private String SERVER_URL;

    private final RestTemplate restTemplate;

    public SecurityClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SessionKey getSessionKey(byte[] publicKey) {
        return restTemplate.exchange(SERVER_URL + "/session-key", HttpMethod.POST,
                new HttpEntity<>(new SessionKeyRequestDto(publicKey)), SessionKey.class)
            .getBody();
    }
}

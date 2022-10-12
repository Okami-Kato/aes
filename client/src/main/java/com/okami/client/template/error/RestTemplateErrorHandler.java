package com.okami.client.template.error;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.okami.client.exception.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Scanner;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestTemplateErrorHandler implements ResponseErrorHandler {
    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException {
        HttpStatus status = clientHttpResponse.getStatusCode();
        return status.is4xxClientError() || status.is5xxServerError();
    }

    @Override
    public void handleError(ClientHttpResponse clientHttpResponse) throws IOException {
        String responseAsString = toString(clientHttpResponse.getBody());
        log.error("ResponseBody: {}", responseAsString);

        throw new ServerException(getMessage(responseAsString));
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        String responseAsString = toString(response.getBody());
        log.error("URL: {}, HttpMethod: {}, ResponseBody: {}", url, method, responseAsString);

        throw new ServerException(getMessage(responseAsString));
    }

    private String toString(InputStream inputStream) {
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    @SneakyThrows
    private String getMessage(String json) {
        JsonNode jsonNode = objectMapper.readTree(json);
        return jsonNode.get("message").textValue();
    }
}


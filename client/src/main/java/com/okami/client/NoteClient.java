package com.okami.client;

import com.okami.client.dto.TextRequestDto;
import com.okami.service.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class NoteClient {

    private final RestTemplate restTemplate;
    @Value("${note-server.url}")
    private String SERVER_URL;

    public NoteDto getNote(String noteName) {
        return restTemplate.exchange(
                SERVER_URL + UriComponentsBuilder.fromPath("/note")
                    .queryParam("noteName", noteName)
                    .build(),
                HttpMethod.GET,
                null,
                NoteDto.class)
            .getBody();
    }

    public String[] getNoteList() {
        return restTemplate.exchange(SERVER_URL + "/notes",
                HttpMethod.GET,
                null,
                String[].class)
            .getBody();
    }


    public void deleteNote(String noteName) {
        restTemplate.exchange(
            SERVER_URL + UriComponentsBuilder.fromPath("/note")
                .queryParam("noteName", noteName)
                .build(),
            HttpMethod.DELETE,
            null,
            Void.class);
    }

    public void updateNote(NoteDto dto) {
        restTemplate.exchange(
            SERVER_URL + UriComponentsBuilder.fromPath("/note")
                .queryParam("noteName", dto.getNoteName())
                .build(),
            HttpMethod.PUT,
            new HttpEntity<>(new TextRequestDto().setText(dto.getText())),
            Void.class);
    }

    public void createNote(String noteName) {
        restTemplate.exchange(
            SERVER_URL + UriComponentsBuilder.fromPath("/note")
                .queryParam("noteName", noteName)
                .build(),
            HttpMethod.POST,
            null,
            Void.class);
    }
}

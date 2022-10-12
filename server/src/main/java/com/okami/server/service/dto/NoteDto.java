package com.okami.server.service.dto;

import lombok.Data;

@Data
public final class NoteDto {

    private String noteName;

    private String username;

    private String text;
}

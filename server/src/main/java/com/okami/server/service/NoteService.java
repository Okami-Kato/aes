package com.okami.server.service;

import com.okami.server.service.dto.NoteDto;

import java.util.List;

public interface NoteService {

    NoteDto getNoteByUsernameAndName(String username, String noteName);

    List<String> getNoteList(String username);

    void deleteNote(String username, String noteName);

    void createNote(NoteDto dto);

    void updateNote(NoteDto dto);
}

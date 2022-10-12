package com.okami.service;

import com.okami.security.exception.SessionKeyException;
import com.okami.service.dto.NoteDto;

import java.util.List;

public interface NoteService {

    NoteDto getNoteByName(String name) throws SessionKeyException;

    List<String> getNoteList();

    void deleteNote(String name);

    void createNote(String noteName);

    void updateNote(NoteDto dto) throws SessionKeyException;
}

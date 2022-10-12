package com.okami.service.impl;

import com.okami.client.NoteClient;
import com.okami.security.exception.SessionKeyException;
import com.okami.security.service.SecurityService;
import com.okami.service.NoteService;
import com.okami.service.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteClient noteClient;

    private final SecurityService securityService;

    @Override
    public NoteDto getNoteByName(String noteName) throws SessionKeyException {
        NoteDto dto = noteClient.getNote(noteName);
        return dto.setText(securityService.decryptText(dto.getText()));
    }

    @Override
    public List<String> getNoteList() {
        String[] notes = noteClient.getNoteList();
        return List.of(notes);
    }

    @Override
    public void deleteNote(String name) {
        noteClient.deleteNote(name);
    }

    @Override
    public void createNote(String noteName) {
        noteClient.createNote(noteName);
    }

    @Override
    public void updateNote(NoteDto dto) throws SessionKeyException {
        noteClient.updateNote(dto.setText(securityService.encryptText(dto.getText())));
    }
}

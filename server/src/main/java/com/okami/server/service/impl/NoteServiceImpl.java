package com.okami.server.service.impl;

import com.okami.server.domain.Note;
import com.okami.server.domain.User;
import com.okami.server.repository.NoteRepository;
import com.okami.server.repository.UserRepository;
import com.okami.server.security.service.SecurityService;
import com.okami.server.service.NoteService;
import com.okami.server.service.dto.NoteDto;
import com.okami.server.service.exception.AlreadyExistsException;
import com.okami.server.service.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    private final UserRepository userRepository;

    private final SecurityService securityService;

    @Override
    public NoteDto getNoteByUsernameAndName(String username, String noteName) {
        Note entity = noteRepository.findByUserUsernameAndName(username, noteName)
            .orElseThrow(() -> new NotFoundException("Note with name \"%s\" not found for user \"%s\"".formatted(noteName, username)));
        return new NoteDto()
            .setNoteName(noteName)
            .setUsername(username)
            .setText(securityService.encryptText(entity.getText(), username));
    }

    @Override
    public List<String> getNoteList(String username) {
        return noteRepository.getNamesByUsername(username);
    }

    @Override
    public void deleteNote(String username, String noteName) {
        noteRepository.deleteByNameAndUserUsername(noteName, username);
    }

    @Override
    public void createNote(NoteDto dto) {
        if (noteRepository.existsByUserUsernameAndName(dto.getUsername(), dto.getNoteName())) {
            throw new AlreadyExistsException("User \"%s\" already has note with name \"%s\"".formatted(dto.getUsername(), dto.getNoteName()));
        }
        User user = userRepository.findByUsername(dto.getUsername())
            .orElseThrow(() -> new NotFoundException("User with username \"%s\" not found".formatted(dto.getUsername())));

        noteRepository.save(
            new Note()
                .setName(dto.getNoteName())
                .setUser(user)
                .setText("")
        );
    }

    @Override
    public void updateNote(NoteDto dto) {
        Note existingNote = noteRepository.findByUserUsernameAndName(dto.getUsername(), dto.getNoteName())
            .orElseThrow(() -> new NotFoundException("User with username \"%s\" not found".formatted(dto.getUsername())));
        existingNote.setText(securityService.decryptText(dto.getText(), dto.getUsername()));
        noteRepository.save(existingNote);
    }
}

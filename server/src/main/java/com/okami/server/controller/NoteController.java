package com.okami.server.controller;

import com.okami.server.controller.dto.TextRequestDto;
import com.okami.server.service.NoteService;
import com.okami.server.service.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/note")
    public NoteDto getNote(@RequestParam String noteName, Principal principal) {
        return noteService.getNoteByUsernameAndName(principal.getName(), noteName);
    }

    @GetMapping("/notes")
    public List<String> getNoteList(Principal principal) {
        return noteService.getNoteList(principal.getName());
    }

    @DeleteMapping("/note")
    public void deleteNote(@RequestParam String noteName, Principal principal) {
        noteService.deleteNote(principal.getName(), noteName);
    }

    @PutMapping("/note")
    public void updateNote(@RequestParam String noteName, @RequestBody TextRequestDto dto, Principal principal) {
        noteService.updateNote(new NoteDto().setUsername(principal.getName()).setNoteName(noteName).setText(dto.text()));
    }

    @PostMapping("/note")
    public void createNote(@RequestParam String noteName, Principal principal) {
        noteService.createNote(new NoteDto().setUsername(principal.getName()).setNoteName(noteName));
    }
}

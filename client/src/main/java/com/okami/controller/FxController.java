package com.okami.controller;

import com.okami.security.exception.SessionKeyException;
import com.okami.security.service.SecurityService;
import com.okami.service.NoteService;
import com.okami.service.dto.NoteDto;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static java.time.Duration.between;
import static javafx.scene.control.Alert.AlertType.*;

@Component
@RequiredArgsConstructor
public class FxController {

    private final SecurityService securityService;

    private final NoteService noteService;

    private Optional<String> currentNote = Optional.empty();

    @FXML
    private TextArea textArea;

    @FXML
    private Text textField;

    @FXML
    private Button deleteButton;

    @FXML
    private Button saveButton;

    @FXML
    void obtainToken(ActionEvent event) {
        Instant expirationTimestamp = securityService.obtainSessionKey();
        beginCountdown(expirationTimestamp);
    }

    @FXML
    void createNote(ActionEvent event) {
        TextInputDialog td = new TextInputDialog("Enter name of note");
        Optional<String> selectedNote = td.showAndWait();

        if (selectedNote.isEmpty()) {
            return;
        }

        currentNote = selectedNote;
        noteService.createNote(selectedNote.get());
        textArea.setText("");
        textArea.setEditable(true);
        deleteButton.setVisible(true);
        saveButton.setVisible(true);
    }

    @FXML
    void openNote(ActionEvent event) {
        Map<String, Supplier<String>> noteRetrieversMap = getNoteRetrieversMap();

        if (noteRetrieversMap.isEmpty()) {
            showAlert(INFORMATION, "You don't have any notes");
            return;
        }

        String[] notes = noteRetrieversMap.keySet().toArray(new String[0]);
        ChoiceDialog<String> d = new ChoiceDialog<>(notes[0], notes);
        Optional<String> selectedNote = d.showAndWait();

        if (selectedNote.isEmpty()) {
            return;
        }
        currentNote = selectedNote;

        textArea.setText(noteRetrieversMap.get(selectedNote.get()).get());
        textArea.setEditable(true);
        deleteButton.setVisible(true);
        saveButton.setVisible(true);
    }

    @FXML
    void saveNote(ActionEvent event) {
        if (currentNote.isEmpty()) {
            showAlert(ERROR, "You didn't select note");
            return;
        }
        String text = textArea.getText();
        try {
            noteService.updateNote(new NoteDto().setNoteName(currentNote.get()).setText(text));
            showAlert(INFORMATION, "Saved");
        } catch (SessionKeyException e) {
            showAlert(ERROR, e.getMessage());
        }
    }

    @FXML
    void deleteNote(ActionEvent event) {
        if (currentNote.isEmpty()) {
            showAlert(ERROR, "You didn't select note");
            return;
        }
        noteService.deleteNote(currentNote.get());
        showAlert(INFORMATION, "Deleted");
        currentNote = Optional.empty();
        textArea.setText("");
        textArea.setEditable(false);
        deleteButton.setVisible(false);
        saveButton.setVisible(false);

    }

    private void showAlert(AlertType alertType, String message) {
        Alert a = new Alert(alertType);
        a.setContentText(message);
        a.show();
    }

    private Map<String, Supplier<String>> getNoteRetrieversMap() {
        List<String> names = noteService.getNoteList();
        Map<String, Supplier<String>> result = new HashMap<>();
        names.forEach(name -> result.put(name, () -> {
            try {
                return noteService.getNoteByName(name).getText();
            } catch (SessionKeyException e) {
                showAlert(ERROR, e.getMessage());
                return "";
            }
        }));
        return result;
    }

    private void beginCountdown(Instant end) {
        AtomicLong counter = new AtomicLong(between(Instant.now(), end).getSeconds());
        Timeline oneSecondWonder = new Timeline(
            new KeyFrame(
                Duration.seconds(1),
                event -> {
                    if (counter.get() == 0) {
                        textField.setText("Token expired");
                    } else {
                        textField.setText("Token expires in " + DurationFormatUtils.formatDuration(counter.getAndDecrement() * 1000, "H:mm:ss", true));
                    }
                }
            )
        );
        oneSecondWonder.setCycleCount((int) counter.get() + 1);
        oneSecondWonder.play();
    }
}

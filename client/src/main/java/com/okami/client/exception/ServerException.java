package com.okami.client.exception;

import javafx.scene.control.Alert;

import java.io.IOException;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class ServerException extends IOException {

    public ServerException(String message) {
        super(message);
        showErrorMessage(message);
    }

    private static void showErrorMessage(String message) {
        Alert a = new Alert(ERROR);
        a.setContentText(message);
        a.show();
    }
}

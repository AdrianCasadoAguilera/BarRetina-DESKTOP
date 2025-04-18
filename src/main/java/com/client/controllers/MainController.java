package com.client.controllers;

import com.client.utils.AppData;
import com.client.utils.Connection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController {

    @FXML
    private TextField urlInput;

    @FXML
    private TextField ubiInput;

    @FXML
    private void connect(ActionEvent evt) {
        System.out.println("Connectant al servidor WebSocket...");
        Connection conn = Connection.getInstance();
        conn.connect(urlInput.getText());

        AppData.saveConfig(ubiInput.getText(), urlInput.getText());
    }
}

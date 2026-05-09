package com.minden.ui.controller;

import com.minden.dto.PlayerDto;
import com.minden.ui.JavaFxApp;
import com.minden.ui.SessionContext;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML private Label userGreetingLabel;
    @FXML private StackPane contentArea;
    
    @FXML private Button btnPlayers;
    @FXML private Button btnMap;
    @FXML private Button btnLogs;
    @FXML private Button btnEvents;
    @FXML private Button btnTreasures;

    @FXML
    public void initialize() {
        // Встановлюємо привітання
        PlayerDto currentUser = SessionContext.getInstance().getCurrentUser();
        if (currentUser != null) {
            userGreetingLabel.setText("Привіт, " + currentUser.getUsername() + "!");
        }

        // Завантажуємо вкладку "Гравці" за замовчуванням
        showPlayers(null);
    }

    @FXML
    private void showPlayers(ActionEvent event) {
        setActiveButton(btnPlayers);
        loadView("/fxml/views/players.fxml");
    }

    @FXML
    private void showMap(ActionEvent event) {
        setActiveButton(btnMap);
        loadView("/fxml/views/map.fxml");
    }

    @FXML
    private void showLogs(ActionEvent event) {
        setActiveButton(btnLogs);
        loadView("/fxml/views/logs.fxml");
    }

    @FXML
    private void showEvents(ActionEvent event) {
        setActiveButton(btnEvents);
        loadView("/fxml/views/event.fxml");
    }

    @FXML
    private void showTreasures(ActionEvent event) {
        setActiveButton(btnTreasures);
        loadView("/fxml/views/treasure.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Не вдалося завантажити view: " + fxmlPath);
        }
    }

    private void setActiveButton(Button activeButton) {
        btnPlayers.getStyleClass().remove("nav-button-active");
        btnMap.getStyleClass().remove("nav-button-active");
        btnLogs.getStyleClass().remove("nav-button-active");
        btnEvents.getStyleClass().remove("nav-button-active");
        btnTreasures.getStyleClass().remove("nav-button-active");

        if (activeButton != null) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionContext.getInstance().logout();
        JavaFxApp.setRoot("login");
    }
}

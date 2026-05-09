package com.minden.ui.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.minden.config.ServiceFactory;
import com.minden.entity.ActionLog;
import com.minden.repository.ActionLogRepository;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LogsController {
    @FXML
    private TableView<ActionLog> logsTable;
    @FXML
    private TableColumn<ActionLog, Integer> idColumn;
    @FXML
    private TableColumn<ActionLog, Integer> playerIdColumn;
    @FXML
    private TableColumn<ActionLog, String> actionTypeColumn;
    @FXML
    private TableColumn<ActionLog, Integer> fromXColumn;
    @FXML
    private TableColumn<ActionLog, Integer> fromYColumn;
    @FXML
    private TableColumn<ActionLog, Integer> toXColumn;
    @FXML
    private TableColumn<ActionLog, Integer> toYColumn;
    @FXML
    private TableColumn<ActionLog, Boolean> isValidColumn;
    @FXML
    private TableColumn<ActionLog, String> createdAtColumn;

    private ActionLogRepository actionLogRepository;
    private ObservableList<ActionLog> logsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            actionLogRepository = ServiceFactory.getInstance().getActionLogRepository();

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            playerIdColumn.setCellValueFactory(new PropertyValueFactory<>("playerId"));
            actionTypeColumn.setCellValueFactory(new PropertyValueFactory<>("actionType"));
            fromXColumn.setCellValueFactory(new PropertyValueFactory<>("fromX"));
            fromYColumn.setCellValueFactory(new PropertyValueFactory<>("fromY"));
            toXColumn.setCellValueFactory(new PropertyValueFactory<>("toX"));
            toYColumn.setCellValueFactory(new PropertyValueFactory<>("toY"));
            isValidColumn.setCellValueFactory(new PropertyValueFactory<>("isValid"));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            createdAtColumn.setCellValueFactory(cellData -> {
                LocalDateTime ldt = cellData.getValue().getCreatedAt();
                if (ldt != null) {
                    return new SimpleStringProperty(ldt.format(formatter));
                }
                return new SimpleStringProperty("");
            });

            logsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося завантажити логи: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    private void loadData() {
        try {
            logsData.clear();
            logsData.addAll(actionLogRepository.findAll());
            logsTable.setItems(logsData);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося оновити логи: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

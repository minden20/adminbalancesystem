package com.minden.ui.controller;

import java.util.Optional;

import com.minden.config.ServiceFactory;
import com.minden.dto.EventDto;
import com.minden.service.EventService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class EventController {
    @FXML
    private TableView<EventDto> eventsTable;
    @FXML
    private TableColumn<EventDto, Integer> idColumn;
    @FXML
    private TableColumn<EventDto, String> nameColumn;
    @FXML
    private TableColumn<EventDto, String> descriptionColumn;
    @FXML
    private TableColumn<EventDto, Integer> minGoldPenaltyColumn;
    @FXML
    private TableColumn<EventDto, Integer> maxGoldPenaltyColumn;

    @FXML
    private VBox editPanel;
    @FXML
    private Label editTitleLabel;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField minGoldPenaltyField;
    @FXML
    private TextField maxGoldPenaltyField;

    private EventService eventService;
    private ObservableList<EventDto> eventsData = FXCollections.observableArrayList();
    private EventDto selectedEvent;
    private boolean isNewEvent;

    @FXML
    public void initialize() {
        try {
            eventService = ServiceFactory.getInstance().getEventService();
            idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
            descriptionColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("description"));
            minGoldPenaltyColumn
                    .setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("minGoldPenalty"));
            maxGoldPenaltyColumn
                    .setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maxGoldPenalty"));
            eventsTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> showEventDetails(newValue));
            eventsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            editPanel.setVisible(false);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        eventsData.clear();
        eventsData.addAll(eventService.findAll());
        eventsTable.setItems(eventsData);
    }

    private void showEventDetails(EventDto event) {
        if (event != null) {
            selectedEvent = event;
            nameField.setText(event.getName());
            descriptionField.setText(event.getDescription());
            minGoldPenaltyField.setText(String.valueOf(event.getMinGoldPenalty()));
            maxGoldPenaltyField.setText(String.valueOf(event.getMaxGoldPenalty()));
            editPanel.setVisible(true);
        } else {
            selectedEvent = null;
            editPanel.setVisible(false);
        }
    }

    @FXML
    private void handleAdd() {
        isNewEvent = true;
        selectedEvent = new EventDto();
        nameField.clear();
        descriptionField.clear();
        minGoldPenaltyField.clear();
        maxGoldPenaltyField.clear();
        editTitleLabel.setText("Нова подія");
        editPanel.setVisible(true);
        eventsTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        loadData();
        editPanel.setVisible(false);
        selectedEvent = null;
        isNewEvent = false;
    }

    @FXML
    private void handleSave() {
        if (selectedEvent != null) {
            String name = nameField.getText();
            String description = descriptionField.getText();
            String minGoldStr = minGoldPenaltyField.getText();
            String maxGoldStr = maxGoldPenaltyField.getText();

            if (name == null || name.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Назва події не може бути порожньою!");
                return;
            }

            if (description == null || description.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Опис події не може бути порожнім!");
                return;
            }

            int minGoldPenalty;
            int maxGoldPenalty;
            try {
                minGoldPenalty = Integer.parseInt(minGoldStr);
                maxGoldPenalty = Integer.parseInt(maxGoldStr);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Помилка вводу", "Мінімальний та максимальний штрафи мають бути цілими числами!");
                return;
            }

            if (minGoldPenalty < 0 || maxGoldPenalty < 0) {
                showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Штрафи не можуть бути від'ємними!");
                return;
            }

            if (minGoldPenalty > maxGoldPenalty) {
                showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Мінімальний штраф не може бути більшим за максимальний!");
                return;
            }

            selectedEvent.setName(name.trim());
            selectedEvent.setDescription(description.trim());
            selectedEvent.setMinGoldPenalty(minGoldPenalty);
            selectedEvent.setMaxGoldPenalty(maxGoldPenalty);

            try {
                if (isNewEvent) {
                    eventService.createEvent(selectedEvent);
                } else {
                    eventService.updateEvent(selectedEvent);
                }
                loadData();
                editPanel.setVisible(false);
                selectedEvent = null;
                isNewEvent = false;
                showAlert(Alert.AlertType.INFORMATION, "Успіх", "Подію успішно збережено.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося зберегти подію: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedEvent != null)
            ;

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Підтвердження видалення");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Ви впевнені, що хочете видалити цю подію?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                eventService.deleteEvent(selectedEvent.getId());
                loadData();
                editPanel.setVisible(false);
                selectedEvent = null;
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося видалити подію: " + e.getMessage());
            }
        } else {
            // Користувач скасував видалення
            showAlert(Alert.AlertType.INFORMATION, "Скасовано", "Видалення події скасовано.");
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
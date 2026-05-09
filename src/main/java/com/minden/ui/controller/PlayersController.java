package com.minden.ui.controller;

import com.minden.config.ServiceFactory;
import com.minden.dto.PlayerDto;
import com.minden.service.PlayerService;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class PlayersController {

    @FXML private TableView<PlayerDto> playersTable;
    @FXML private TableColumn<PlayerDto, Integer> idColumn;
    @FXML private TableColumn<PlayerDto, String> usernameColumn;
    @FXML private TableColumn<PlayerDto, String> emailColumn;
    @FXML private TableColumn<PlayerDto, Integer> goldColumn;
    @FXML private TableColumn<PlayerDto, Integer> energyColumn;

    // Права панель (редагування)
    @FXML private VBox editPanel;
    @FXML private Label editTitleLabel;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextField goldField;
    @FXML private TextField energyField;
    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private TextField searchField;

    private PlayerService playerService;
    private ObservableList<PlayerDto> playersData = FXCollections.observableArrayList();
    private javafx.collections.transformation.FilteredList<PlayerDto> filteredData;
    private PlayerDto selectedPlayer;

    @FXML
    public void initialize() {
        try {
            playerService = ServiceFactory.getInstance().getPlayerService();

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            goldColumn.setCellValueFactory(new PropertyValueFactory<>("gold"));
            energyColumn.setCellValueFactory(new PropertyValueFactory<>("energy"));

            // Обробник вибору рядка в таблиці
            playersTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> showPlayerDetails(newValue));

            // Забираємо зайву порожню колонку справа
            playersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            // Налаштування фільтрації/пошуку
            filteredData = new FilteredList<>(playersData, p -> true);
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(player -> {
                    if (newValue == null || newValue.trim().isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    if (player.getUsername() != null && player.getUsername().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    if (player.getEmail() != null && player.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    return false;
                });
            });
            playersTable.setItems(filteredData);

            editPanel.setVisible(false);
            loadData();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося ініціалізувати дані: " + e.getMessage());
        }
    }

    private void loadData() {
        playersData.clear();
        playersData.addAll(playerService.findAll());
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
        editPanel.setVisible(false);
        selectedPlayer = null;
    }

    private void showPlayerDetails(PlayerDto player) {
        if (player != null) {
            selectedPlayer = player;
            editPanel.setVisible(true);
            editTitleLabel.setText("Редагування гравця ID: " + player.getId());
            
            usernameField.setText(player.getUsername());
            emailField.setText(player.getEmail());
            goldField.setText(String.valueOf(player.getGold()));
            energyField.setText(String.valueOf(player.getEnergy()));
            xField.setText(String.valueOf(player.getX()));
            yField.setText(String.valueOf(player.getY()));
        } else {
            editPanel.setVisible(false);
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        if (selectedPlayer == null) return;

        try {
            // Оновлюємо DTO з полів
            selectedPlayer.setUsername(usernameField.getText());
            selectedPlayer.setEmail(emailField.getText());
            selectedPlayer.setGold(Integer.parseInt(goldField.getText()));
            selectedPlayer.setEnergy(Integer.parseInt(energyField.getText()));
            selectedPlayer.setX(Integer.parseInt(xField.getText()));
            selectedPlayer.setY(Integer.parseInt(yField.getText()));

            // Зберігаємо через сервіс (там працює валідація!)
            playerService.update(selectedPlayer);
            
            showAlert(Alert.AlertType.INFORMATION, "Успіх", "Дані гравця оновлено.");
            loadData(); // Оновлюємо таблицю
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Помилка вводу", "Золото, Енергія та Координати мають бути числами!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Помилка валідації", e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedPlayer == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Підтвердження видалення");
        confirm.setHeaderText("Ви впевнені, що хочете видалити гравця " + selectedPlayer.getUsername() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                playerService.delete(selectedPlayer.getId());
                loadData();
                editPanel.setVisible(false);
                showAlert(Alert.AlertType.INFORMATION, "Видалено", "Гравця успішно видалено.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося видалити гравця: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

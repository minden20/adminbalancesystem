package com.minden.ui.controller;

import java.util.Optional;

import com.minden.config.ServiceFactory;
import com.minden.dto.TreasureDto;
import com.minden.entity.MapTile;
import com.minden.service.TreasureService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class TreasureController {
    @FXML
    private TableView<TreasureDto> treasuresTable;
    @FXML
    private TableColumn<TreasureDto, Integer> idColumn;
    @FXML
    private TableColumn<TreasureDto, Integer> xColumn;
    @FXML
    private TableColumn<TreasureDto, Integer> yColumn;
    @FXML
    private TableColumn<TreasureDto, Integer> minGoldColumn;
    @FXML
    private TableColumn<TreasureDto, Integer> maxGoldColumn;
    @FXML
    private TableColumn<TreasureDto, Boolean> isCollectedColumn;

    @FXML
    private VBox editPanel;
    @FXML
    private Label editTitleLabel;
    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    @FXML
    private TextField minGoldField;
    @FXML
    private TextField maxGoldField;
    @FXML
    private CheckBox isCollectedCheckBox;

    private TreasureService treasureService;
    private ObservableList<TreasureDto> treasuresData = FXCollections.observableArrayList();
    private TreasureDto selectedTreasure;
    private boolean isNewTreasure;

    @FXML
    public void initialize() {
        try {
            treasureService = ServiceFactory.getInstance().getTreasureService();
            idColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
            xColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("x"));
            yColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("y"));
            minGoldColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("minGold"));
            maxGoldColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maxGold"));
            isCollectedColumn.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("isCollected"));

            treasuresTable.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> showTreasureDetails(newValue));
            treasuresTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            editPanel.setVisible(false);
            loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        treasuresData.clear();
        treasuresData.addAll(treasureService.findAll());
        treasuresTable.setItems(treasuresData);
    }

    private void showTreasureDetails(TreasureDto treasure) {
        if (treasure != null) {
            selectedTreasure = treasure;
            xField.setText(String.valueOf(treasure.getX()));
            yField.setText(String.valueOf(treasure.getY()));
            minGoldField.setText(String.valueOf(treasure.getMinGold()));
            maxGoldField.setText(String.valueOf(treasure.getMaxGold()));
            isCollectedCheckBox.setSelected(treasure.getIsCollected() != null && treasure.getIsCollected());
            editTitleLabel.setText("Редагування скарбу");
            editPanel.setVisible(true);
        } else {
            selectedTreasure = null;
            editPanel.setVisible(false);
        }
    }

    @FXML
    private void handleAdd() {
        isNewTreasure = true;
        selectedTreasure = new TreasureDto();
        xField.clear();
        yField.clear();
        minGoldField.clear();
        maxGoldField.clear();
        isCollectedCheckBox.setSelected(false);
        editTitleLabel.setText("Новий скарб");
        editPanel.setVisible(true);
        treasuresTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleRefresh() {
        loadData();
        editPanel.setVisible(false);
        selectedTreasure = null;
        isNewTreasure = false;
    }

    @FXML
    private void handleSave() {
        if (selectedTreasure != null) {
            String xStr = xField.getText();
            String yStr = yField.getText();
            String minGoldStr = minGoldField.getText();
            String maxGoldStr = maxGoldField.getText();

            int x;
            int y;
            int minGold;
            int maxGold;

            try {
                x = Integer.parseInt(xStr);
                y = Integer.parseInt(yStr);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Помилка вводу", "Координати X та Y мають бути цілими числами!");
                return;
            }

            try {
                minGold = Integer.parseInt(minGoldStr);
                maxGold = Integer.parseInt(maxGoldStr);
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Помилка вводу", "Мінімальна та максимальна кількість золота мають бути цілими числами!");
                return;
            }

            if (x < 0 || y < 0) {
                showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Координати не можуть бути негативними!");
                return;
            }

            if (minGold < 0 || maxGold < 0) {
                showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Кількість золота не може бути негативною!");
                return;
            }

            if (minGold > maxGold) {
                showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Мінімальне золото не може бути більшим за максимальне!");
                return;
            }

            // Додаткова перевірка на тип тайла на карті
            try {
                var mapTileRepo = ServiceFactory.getInstance().getMapTileRepository();
                Optional<MapTile> tileOpt = mapTileRepo.findByCoordinates(x, y);
                if (tileOpt.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Клітинка з координатами (" + x + ", " + y + ") не існує на карті!");
                    return;
                }

                MapTile tile = tileOpt.get();
                if ("Water".equalsIgnoreCase(tile.getTerrainType())) {
                    showAlert(Alert.AlertType.ERROR, "Помилка валідації", "Не можна розмістити скарб на воді (тип тайла: Water)!");
                    return;
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося перевірити тип тайла на карті: " + e.getMessage());
                return;
            }

            selectedTreasure.setX(x);
            selectedTreasure.setY(y);
            selectedTreasure.setMinGold(minGold);
            selectedTreasure.setMaxGold(maxGold);
            selectedTreasure.setIsCollected(isCollectedCheckBox.isSelected());

            try {
                if (isNewTreasure) {
                    treasureService.createTreasure(selectedTreasure);
                } else {
                    treasureService.updateTreasure(selectedTreasure);
                }
                loadData();
                editPanel.setVisible(false);
                selectedTreasure = null;
                isNewTreasure = false;
                showAlert(Alert.AlertType.INFORMATION, "Успіх", "Скарб успішно збережено.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося зберегти скарб: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedTreasure != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Підтвердження видалення");
            confirmationAlert.setHeaderText(null);
            confirmationAlert.setContentText("Ви впевнені, що хочете видалити цей скарб?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    treasureService.deleteTreasure(selectedTreasure.getId());
                    loadData();
                    editPanel.setVisible(false);
                    selectedTreasure = null;
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Помилка", "Не вдалося видалити скарб: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Скасовано", "Видалення скарбу скасовано.");
            }
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

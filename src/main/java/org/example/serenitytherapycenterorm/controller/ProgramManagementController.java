package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.ProgramBO;
import org.example.serenitytherapycenterorm.dto.TherapyProgramDTO;
import org.example.serenitytherapycenterorm.exception.ValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProgramManagementController {

    @FXML private TextField txtProgramName;
    @FXML private TextField txtProgramDuration;
    @FXML private TextField txtTotalSessions;
    @FXML private TextField txtProgramFee;
    @FXML private TextArea txtProgramDescription;
    @FXML private TextField txtSearchProgram;

    @FXML private TableView<TherapyProgramDTO> tblPrograms;
    @FXML private TableColumn<TherapyProgramDTO, Long> colProgramId;
    @FXML private TableColumn<TherapyProgramDTO, String> colProgramName;
    @FXML private TableColumn<TherapyProgramDTO, String> colProgramDuration;
    @FXML private TableColumn<TherapyProgramDTO, Integer> colTotalSessions;
    @FXML private TableColumn<TherapyProgramDTO, BigDecimal> colProgramFee;
    @FXML private TableColumn<TherapyProgramDTO, String> colProgramDescription;

    private final ProgramBO programBO = (ProgramBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PROGRAM);
    private ObservableList<TherapyProgramDTO> masterDataList = FXCollections.observableArrayList();
    private FilteredList<TherapyProgramDTO> filteredDataList;
    private Long selectedProgramId = null;

    @FXML
    public void initialize() {
        colProgramId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProgramName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProgramDuration.setCellValueFactory(new PropertyValueFactory<>("duration"));
        colTotalSessions.setCellValueFactory(new PropertyValueFactory<>("totalSessions"));
        colProgramFee.setCellValueFactory(new PropertyValueFactory<>("fee"));
        colProgramDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        loadAllPrograms();
        setupSearchLogic();
        setupTableSelection();
    }

    private void loadAllPrograms() {
        try {
            List<TherapyProgramDTO> list = programBO.getAllPrograms();
            masterDataList.clear();
            masterDataList.addAll(list);

            filteredDataList = new FilteredList<>(masterDataList, p -> true);
            tblPrograms.setItems(filteredDataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearchLogic() {
        txtSearchProgram.textProperty().addListener((observable, oldValue, newValue) -> {
            if (filteredDataList == null) return;
            filteredDataList.setPredicate(dto -> {
                if (newValue == null || newValue.trim().isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (String.valueOf(dto.getId()).contains(lowerCaseFilter)) return true;
                return dto.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void setupTableSelection() {
        tblPrograms.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedProgramId = newSelection.getId();
                txtProgramName.setText(newSelection.getName());
                txtProgramDuration.setText(newSelection.getDuration());
                txtTotalSessions.setText(String.valueOf(newSelection.getTotalSessions()));
                txtProgramFee.setText(newSelection.getFee().toString());
                txtProgramDescription.setText(newSelection.getDescription());
            }
        });
    }

    @FXML
    void handleSaveProgram(ActionEvent event) {
        try {

            if (txtProgramName.getText().trim().isEmpty() || txtProgramDuration.getText().trim().isEmpty() ||
                    txtTotalSessions.getText().trim().isEmpty() || txtProgramFee.getText().trim().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields except description are required!");
                return;
            }

            TherapyProgramDTO dto = new TherapyProgramDTO(
                    null,
                    txtProgramName.getText().trim(),
                    txtProgramDuration.getText().trim(),
                    new BigDecimal(txtProgramFee.getText().trim()),
                    Integer.parseInt(txtTotalSessions.getText().trim()),
                    txtProgramDescription.getText().trim()
            );

            if (programBO.saveProgram(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Program saved successfully!");
                loadAllPrograms();
                handleClearProgram(null);
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Sessions and Fee must be numeric values!");
        } catch (ValidationException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error: " + e.getMessage());
        }
    }

    @FXML
    void handleUpdateProgram(ActionEvent event) {
        if (selectedProgramId == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a program from the table to update.");
            return;
        }

        try {
            TherapyProgramDTO dto = new TherapyProgramDTO(
                    selectedProgramId,
                    txtProgramName.getText().trim(),
                    txtProgramDuration.getText().trim(),
                    new BigDecimal(txtProgramFee.getText().trim()),
                    Integer.parseInt(txtTotalSessions.getText().trim()),
                    txtProgramDescription.getText().trim()
            );

            if (programBO.updateProgram(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Program updated successfully!");
                loadAllPrograms();
                handleClearProgram(null);
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Sessions and Fee must be numeric values!");
        } catch (ValidationException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML
    void handleDeleteProgram(ActionEvent event) {
        TherapyProgramDTO selected = tblPrograms.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a program from the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + selected.getName() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                if (programBO.deleteProgram(selected.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Program deleted successfully.");
                    loadAllPrograms();
                    handleClearProgram(null);
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete program. It may be linked with patients.");
            }
        }
    }

    @FXML
    void handleClearProgram(ActionEvent event) {
        selectedProgramId = null;
        txtProgramName.clear();
        txtProgramDuration.clear();
        txtTotalSessions.clear();
        txtProgramFee.clear();
        txtProgramDescription.clear();
        tblPrograms.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

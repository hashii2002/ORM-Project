package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.TherapistBO;
import org.example.serenitytherapycenterorm.dto.TherapistDTO;
import org.example.serenitytherapycenterorm.entity.Therapist;
import org.example.serenitytherapycenterorm.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TherapistController {

    @FXML private TextField txtTherapistName;
    @FXML private TextField txtTherapistPhone;
    @FXML private TextField txtTherapistEmail;
    @FXML private TextField txtSearchTherapist;

    @FXML private ComboBox<Therapist.Specialty> cmbTherapistSpecialty;
    @FXML private ComboBox<Therapist.Status> cmbTherapistStatus;
    @FXML private ComboBox<String> cmbStatusSearch;

    @FXML private TableView<TherapistDTO> tblTherapists;
    @FXML private TableColumn<TherapistDTO, Long> colTherapistId;
    @FXML private TableColumn<TherapistDTO, String> colTherapistName;
    @FXML private TableColumn<TherapistDTO, Therapist.Specialty> colTherapistSpecialty;
    @FXML private TableColumn<TherapistDTO, String> colTherapistPhone;
    @FXML private TableColumn<TherapistDTO, String> colTherapistEmail;
    @FXML private TableColumn<TherapistDTO, Therapist.Status> colTherapistStatus;

    private final TherapistBO therapistBO = (TherapistBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.THERAPIST);
    private ObservableList<TherapistDTO> masterDataList = FXCollections.observableArrayList();
    private FilteredList<TherapistDTO> filteredDataList;
    private Long selectedTherapistId = null;

    @FXML
    public void initialize() {

        colTherapistId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTherapistName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colTherapistSpecialty.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        colTherapistPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colTherapistEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTherapistStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        cmbTherapistSpecialty.setItems(FXCollections.observableArrayList(Therapist.Specialty.values()));
        cmbTherapistStatus.setItems(FXCollections.observableArrayList(Therapist.Status.values()));

        loadStatusSearchComboBox();
        loadAllTherapists();
        setupSearchAndFilterLogic();
        setupTableSelection();
    }

    private void loadStatusSearchComboBox() {
        List<String> statuses = new ArrayList<>();
        statuses.add("All Status");
        for (Therapist.Status s : Therapist.Status.values()) {
            statuses.add(s.name());
        }
        cmbStatusSearch.setItems(FXCollections.observableArrayList(statuses));
        cmbStatusSearch.getSelectionModel().selectFirst();
    }

    private void loadAllTherapists() {
        try {
            List<TherapistDTO> list = therapistBO.getAllTherapists();
            masterDataList.clear();
            masterDataList.addAll(list);

            filteredDataList = new FilteredList<>(masterDataList, p -> true);
            tblTherapists.setItems(filteredDataList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupSearchAndFilterLogic() {
        cmbStatusSearch.getSelectionModel().selectedItemProperty().addListener((obj, oldV, newV) -> {
            if (filteredDataList == null) return;
            filteredDataList.setPredicate(dto -> {
                if (newV == null || newV.equals("All Status")) return true;
                return dto.getStatus().name().equals(newV);
            });
        });

        txtSearchTherapist.setOnAction(event -> {
            String text = txtSearchTherapist.getText().trim();
            if (text.isEmpty()) {
                loadAllTherapists();
                return;
            }

            try {
                if (text.matches("\\d+")) {
                    TherapistDTO dto = therapistBO.searchTherapistById(Long.parseLong(text));
                    if (dto != null) {
                        tblTherapists.getSelectionModel().select(dto);
                        tblTherapists.scrollTo(dto);
                    }
                } else {
                    List<TherapistDTO> list = therapistBO.searchTherapistByFullName(text);
                    if (!list.isEmpty()) {
                        for (TherapistDTO t : tblTherapists.getItems()) {
                            if (t.getFullName().toLowerCase().contains(list.get(0).getFullName().toLowerCase())) {
                                tblTherapists.getSelectionModel().select(t);
                                tblTherapists.scrollTo(t);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void setupTableSelection() {
        tblTherapists.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedTherapistId = newSelection.getId();
                txtTherapistName.setText(newSelection.getFullName());
                txtTherapistPhone.setText(newSelection.getPhone());
                txtTherapistEmail.setText(newSelection.getEmail());
                cmbTherapistSpecialty.setValue(newSelection.getSpecialty());
                cmbTherapistStatus.setValue(newSelection.getStatus());
            }
        });
    }

    @FXML
    void handleSaveTherapist(ActionEvent event) {
        try {
            TherapistDTO dto = new TherapistDTO(
                    null,
                    txtTherapistName.getText().trim(),
                    cmbTherapistSpecialty.getValue(),
                    txtTherapistPhone.getText().trim(),
                    txtTherapistEmail.getText().trim(),
                    cmbTherapistStatus.getValue()
            );

            if (therapistBO.saveTherapist(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Therapist saved successfully!");
                loadAllTherapists();
                handleClearTherapist(null);
            }
        } catch (ValidationException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Database Error: " + e.getMessage());
        }
    }

    @FXML
    void handleUpdateTherapist(ActionEvent event) {
        if (selectedTherapistId == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a therapist from the table to update.");
            return;
        }

        try {
            TherapistDTO dto = new TherapistDTO(
                    selectedTherapistId,
                    txtTherapistName.getText().trim(),
                    cmbTherapistSpecialty.getValue(),
                    txtTherapistPhone.getText().trim(),
                    txtTherapistEmail.getText().trim(),
                    cmbTherapistStatus.getValue()
            );

            if (therapistBO.updateTherapist(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Therapist details updated successfully!");
                loadAllTherapists();
                handleClearTherapist(null);
            }
        } catch (ValidationException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Update failed: " + e.getMessage());
        }
    }

    @FXML
    void handleDeleteTherapist(ActionEvent event) {
        TherapistDTO selected = tblTherapists.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Please select a therapist from the table to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete Dr. " + selected.getFullName() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                if (therapistBO.deleteTherapist(selected.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Deleted", "Therapist deleted successfully.");
                    loadAllTherapists();
                    handleClearTherapist(null);
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not delete therapist.");
            }
        }
    }

    @FXML
    void handleClearTherapist(ActionEvent event) {
        selectedTherapistId = null;
        txtTherapistName.clear();
        txtTherapistPhone.clear();
        txtTherapistEmail.clear();
        cmbTherapistSpecialty.getSelectionModel().clearSelection();
        cmbTherapistStatus.getSelectionModel().clearSelection();
        tblTherapists.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.serenitytherapycenterorm.bo.custom.PatientBO;
import org.example.serenitytherapycenterorm.bo.custom.ProgramBO;
import org.example.serenitytherapycenterorm.bo.custom.impl.PatientBOImpl;
import org.example.serenitytherapycenterorm.bo.custom.impl.ProgramBOImpl;
import org.example.serenitytherapycenterorm.dto.PatientDTO;
import org.example.serenitytherapycenterorm.dto.TherapyProgramDTO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PatientController {

    @FXML private Button btnAddNewPatient;
    @FXML private TableView<PatientDTO> tblPatient;
    @FXML private TableColumn<PatientDTO, Long> colId;
    @FXML private TableColumn<PatientDTO, String> colName;
    @FXML private TableColumn<PatientDTO, String> colContact;
    @FXML private TableColumn<PatientDTO, String> colDOB;
    @FXML private TableColumn<PatientDTO, String> colAddress;
    @FXML private TableColumn<PatientDTO, String> colPrograms;
    @FXML private TableColumn<PatientDTO, String> colStatus;
    @FXML private TableColumn<PatientDTO, String> colNote;

    @FXML private Label lblTotalPatients;
    @FXML private Label lblInactivePatients;
    @FXML private Label lblCompletedTreatments;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbFilterStatus;
    @FXML private ComboBox<String> cmbFilterPrograms;

    private final PatientBO patientBO = new PatientBOImpl();
    private final ProgramBO programBO = new ProgramBOImpl();

    private final ObservableList<PatientDTO> patientList = FXCollections.observableArrayList();
    private FilteredList<PatientDTO> filteredList;

    public void initialize() {
        // Table Columns Mapping
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("dobString"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPrograms.setCellValueFactory(new PropertyValueFactory<>("programsDisplay"));
        colNote.setCellValueFactory(new PropertyValueFactory<>("interviewNote"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tblPatient.setEditable(true);
        setupEditableColumns();

        loadAllPatients();
        initFilterComboBoxes();

        cmbFilterStatus.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cmbFilterPrograms.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    public void loadAllPatients() {
        patientList.clear();
        try {
            List<PatientDTO> allPatients = patientBO.getAllPatients();
            patientList.addAll(allPatients);

            filteredList = new FilteredList<>(patientList, p -> true);
            tblPatient.setItems(filteredList);

            calculateAnalytics(allPatients);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateAnalytics(List<PatientDTO> list) {
        long total = list.size();
        long inactive = list.stream().filter(p -> "Inactive".equalsIgnoreCase(p.getStatus())).count();
        long completed = list.stream().filter(p -> "Completed".equalsIgnoreCase(p.getStatus())).count();

        lblTotalPatients.setText(String.valueOf(total));
        lblInactivePatients.setText(String.valueOf(inactive));
        lblCompletedTreatments.setText(String.valueOf(completed));
    }

    private void initFilterComboBoxes() {
        cmbFilterStatus.setItems(FXCollections.observableArrayList("All Status", "Active", "Inactive", "Completed"));
        cmbFilterStatus.getSelectionModel().selectFirst();

        ObservableList<String> programNames = FXCollections.observableArrayList();
        programNames.add("All Programs");
        try {
            List<TherapyProgramDTO> allPrograms = programBO.getAllPrograms();
            for (TherapyProgramDTO p : allPrograms) {
                programNames.add(p.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        cmbFilterPrograms.setItems(programNames);
        cmbFilterPrograms.getSelectionModel().selectFirst();
    }

    private void applyFilters() {
        String selectedStatus = cmbFilterStatus.getSelectionModel().getSelectedItem();
        String selectedProgram = cmbFilterPrograms.getSelectionModel().getSelectedItem();

        filteredList.setPredicate(patient -> {
            boolean matchesStatus = (selectedStatus == null || "All Status".equals(selectedStatus) ||
                    selectedStatus.equalsIgnoreCase(patient.getStatus()));

            boolean matchesProgram = (selectedProgram == null || "All Programs".equals(selectedProgram) ||
                    (patient.getProgramsDisplay() != null && patient.getProgramsDisplay().contains(selectedProgram)));

            return matchesStatus && matchesProgram;
        });
    }

    @FXML
    void handleSearchEnter(ActionEvent event) {
        String searchText = txtSearch.getText().trim().toLowerCase();
        if (searchText.isEmpty()) return;

        for (PatientDTO patient : tblPatient.getItems()) {
            if (String.valueOf(patient.getId()).equals(searchText) ||
                    patient.getName().toLowerCase().contains(searchText)) {

                tblPatient.getSelectionModel().select(patient);
                tblPatient.scrollTo(patient);
                return;
            }
        }
        new Alert(Alert.AlertType.INFORMATION, "No Patient found with the given Name or ID!").show();
    }

    @FXML
    void handleDeletePatientClick(ActionEvent event) {
        PatientDTO selectedPatient = tblPatient.getSelectionModel().getSelectedItem();

        if (selectedPatient == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a patient row from the table to delete!").show();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete Patient: " + selectedPatient.getName() + "?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                patientList.remove(selectedPatient);
                calculateAnalytics(patientList);
                new Alert(Alert.AlertType.INFORMATION, "Patient deleted successfully!").show();
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error occurred while deleting patient!").show();
            }
        }
    }

    private void setupEditableColumns() {
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(event -> {
            PatientDTO p = event.getRowValue();
            p.setName(event.getNewValue());
            updatePatientInDB(p);
        });

        colContact.setCellFactory(TextFieldTableCell.forTableColumn());
        colContact.setOnEditCommit(event -> {
            PatientDTO p = event.getRowValue();
            p.setPhone(event.getNewValue());
            updatePatientInDB(p);
        });

        colAddress.setCellFactory(TextFieldTableCell.forTableColumn());
        colAddress.setOnEditCommit(event -> {
            PatientDTO p = event.getRowValue();
            p.setAddress(event.getNewValue());
            updatePatientInDB(p);
        });

        colStatus.setCellFactory(TextFieldTableCell.forTableColumn());
        colStatus.setOnEditCommit(event -> {
            PatientDTO p = event.getRowValue();
            p.setStatus(event.getNewValue());
            updatePatientInDB(p);
            calculateAnalytics(patientList);
        });
    }

    private void updatePatientInDB(PatientDTO dto) {
        try {
            patientBO.savePatient(dto);
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to update data in Database!").show();
            loadAllPatients();
        }
    }

    @FXML
    void handleAddNewPatientClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddNewPatient.fxml"));
            Parent root = loader.load();

            AddNewPatientController addNewPatientController = loader.getController();
            addNewPatientController.setParentController(this);

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(currentStage);
            popupStage.initStyle(StageStyle.DECORATED);
            popupStage.setTitle("Serenity Therapy Center - Patient Registration");

            BoxBlur blur = new BoxBlur(3, 3, 3);
            currentStage.getScene().getRoot().setEffect(blur);
            popupStage.setOnHidden(e -> currentStage.getScene().getRoot().setEffect(null));

            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.setHeight(800);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

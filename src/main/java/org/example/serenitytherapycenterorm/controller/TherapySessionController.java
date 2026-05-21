package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.PatientBO;
import org.example.serenitytherapycenterorm.bo.custom.ProgramBO;
import org.example.serenitytherapycenterorm.bo.custom.TherapistBO;
import org.example.serenitytherapycenterorm.bo.custom.TherapySessionBO;
import org.example.serenitytherapycenterorm.dto.PatientDTO;
import org.example.serenitytherapycenterorm.dto.TherapyProgramDTO;
import org.example.serenitytherapycenterorm.dto.TherapistDTO;
import org.example.serenitytherapycenterorm.dto.TherapySessionDTO;
import org.example.serenitytherapycenterorm.exception.ValidationException;

import java.util.List;
import java.util.Optional;

public class TherapySessionController {

    @FXML private Label lblTotalSessions;
    @FXML private Label lblConfirmedSessions;
    @FXML private ComboBox<String> cmbPatient;
    @FXML private ComboBox<String> cmbProgram;
    @FXML private ComboBox<String> cmbTherapist;
    @FXML private DatePicker dtpSessionDate;
    @FXML private ComboBox<String> cmbTimeSlot;
    @FXML private Button btnClear;
    @FXML private Button btnSchedule;
    @FXML private TextField txtSearchSession;
    @FXML private ComboBox<String> cmbFilterProgramTable;
    @FXML private Button btnCancelSession;

    @FXML private TableView<TherapySessionDTO> tblSessions;
    @FXML private TableColumn<TherapySessionDTO, Long> colSessionId;
    @FXML private TableColumn<TherapySessionDTO, String> colPatientName;
    @FXML private TableColumn<TherapySessionDTO, String> colProgramName;
    @FXML private TableColumn<TherapySessionDTO, String> colTherapistName;
    @FXML private TableColumn<TherapySessionDTO, String> colDate;
    @FXML private TableColumn<TherapySessionDTO, String> colTimeSlot;
    @FXML private TableColumn<TherapySessionDTO, String> colSessionStatus;

    private final TherapySessionBO sessionBO = (TherapySessionBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.SESSION);
    private final PatientBO patientBO = (PatientBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PATIENT);
    private final ProgramBO programBO = (ProgramBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PROGRAM);
    private final TherapistBO therapistBO = (TherapistBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.THERAPIST);

    private final ObservableList<TherapySessionDTO> sessionList = FXCollections.observableArrayList();
    private FilteredList<TherapySessionDTO> filteredList;

    private List<PatientDTO> allPatients;
    private List<TherapyProgramDTO> allPrograms;
    private List<TherapistDTO> allTherapists;

    public void initialize() {
        // Table Mapping
        colSessionId.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colProgramName.setCellValueFactory(new PropertyValueFactory<>("programName"));
        colTherapistName.setCellValueFactory(new PropertyValueFactory<>("therapistName"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateString"));
        colTimeSlot.setCellValueFactory(new PropertyValueFactory<>("timeSlot"));
        colSessionStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadFormComboBoxes();
        loadAllSessions();

        cmbFilterProgramTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> applyTableFilters());
        txtSearchSession.textProperty().addListener((obs, oldV, newV) -> applyTableFilters());

        tblSessions.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFormForUpdate(newSelection);
            }
        });
    }

    private void loadFormComboBoxes() {
        try {
            allPatients = patientBO.getAllPatients();
            ObservableList<String> patients = FXCollections.observableArrayList();
            for (PatientDTO p : allPatients) patients.add(p.getId() + " - " + p.getName());
            cmbPatient.setItems(patients);

            allPrograms = programBO.getAllPrograms();
            ObservableList<String> programs = FXCollections.observableArrayList();
            ObservableList<String> tableFilterPrograms = FXCollections.observableArrayList("All Programs");
            for (TherapyProgramDTO pr : allPrograms) {
                programs.add(pr.getId() + " - " + pr.getName());
                tableFilterPrograms.add(pr.getName());
            }
            cmbProgram.setItems(programs);
            cmbFilterProgramTable.setItems(tableFilterPrograms);
            cmbFilterProgramTable.getSelectionModel().selectFirst();

            allTherapists = therapistBO.getAllTherapists();
            ObservableList<String> therapists = FXCollections.observableArrayList();
            for (TherapistDTO t : allTherapists) therapists.add(t.getId() + " - " + t.getFullName());
            cmbTherapist.setItems(therapists);

            cmbTimeSlot.setItems(FXCollections.observableArrayList(
                    "08:00 AM - 09:00 AM", "09:00 AM - 10:00 AM", "10:00 AM - 11:00 AM",
                    "11:00 AM - 12:00 PM", "02:00 PM - 03:00 PM", "03:00 PM - 04:00 PM","04:00 PM - 05:00 PM","05:00 PM - 06:00 PM"
            ));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadAllSessions() {
        sessionList.clear();
        try {
            List<TherapySessionDTO> list = sessionBO.getAllSessions();
            sessionList.addAll(list);
            filteredList = new FilteredList<>(sessionList, p -> true);
            tblSessions.setItems(filteredList);

            calculateAnalytics(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void calculateAnalytics(List<TherapySessionDTO> list) {
        long total = list.size();
        long confirmed = list.stream().filter(s -> "Confirmed".equalsIgnoreCase(s.getStatus())).count();
        lblTotalSessions.setText(String.valueOf(total));
        lblConfirmedSessions.setText(String.valueOf(confirmed));
    }

    private void applyTableFilters() {
        String selectedProgram = cmbFilterProgramTable.getSelectionModel().getSelectedItem();
        String searchText = txtSearchSession.getText().trim().toLowerCase();

        filteredList.setPredicate(session -> {
            boolean matchesProgram = (selectedProgram == null || "All Programs".equals(selectedProgram) ||
                    session.getProgramName().equalsIgnoreCase(selectedProgram));

            boolean matchesSearch = (searchText.isEmpty() ||
                    String.valueOf(session.getSessionId()).contains(searchText) ||
                    session.getPatientName().toLowerCase().contains(searchText));

            return matchesProgram && matchesSearch;
        });
    }

    @FXML
    void handleScheduleSession(ActionEvent event) {
        try {
            TherapySessionDTO dto = new TherapySessionDTO();

            TherapySessionDTO selectedRow = tblSessions.getSelectionModel().getSelectedItem();
            if (selectedRow != null) {
                dto.setSessionId(selectedRow.getSessionId());
            }

            if (cmbPatient.getValue() != null) {
                dto.setPatientId(Long.parseLong(cmbPatient.getValue().split(" - ")[0]));
            }
            if (cmbProgram.getValue() != null) {
                dto.setProgramId(Long.parseLong(cmbProgram.getValue().split(" - ")[0]));
            }
            if (cmbTherapist.getValue() != null) {
                dto.setTherapistId(Long.parseLong(cmbTherapist.getValue().split(" - ")[0]));
            }

            dto.setSessionDate(dtpSessionDate.getValue());
            dto.setTimeSlot(cmbTimeSlot.getValue());
            dto.setStatus("Confirmed");

            boolean isSaved = sessionBO.scheduleSession(dto);
            if (isSaved) {
                new Alert(Alert.AlertType.INFORMATION, "Therapy Session scheduled successfully!").show();
                handleClearForm(null);
                loadAllSessions();
            }
        } catch (ValidationException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage()).show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "An error occurred while saving the session!").show();
        }
    }

    @FXML
    void handleCancelSessionClick(ActionEvent event) {
        TherapySessionDTO selected = tblSessions.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a session from the table to cancel!").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this session?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try {
                boolean isCancelled = sessionBO.cancelSession(selected.getSessionId());
                if (isCancelled) {
                    new Alert(Alert.AlertType.INFORMATION, "Session cancelled successfully!").show();
                    loadAllSessions();
                    handleClearForm(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        cmbPatient.getSelectionModel().clearSelection();
        cmbProgram.getSelectionModel().clearSelection();
        cmbTherapist.getSelectionModel().clearSelection();
        dtpSessionDate.setValue(null);
        cmbTimeSlot.getSelectionModel().clearSelection();
        tblSessions.getSelectionModel().clearSelection();
    }

    private void populateFormForUpdate(TherapySessionDTO dto) {

        for (String item : cmbPatient.getItems()) {
            if (item.startsWith(dto.getPatientId() + " - ")) { cmbPatient.setValue(item); break; }
        }
        for (String item : cmbProgram.getItems()) {
            if (item.startsWith(dto.getProgramId() + " - ")) { cmbProgram.setValue(item); break; }
        }
        for (String item : cmbTherapist.getItems()) {
            if (item.startsWith(dto.getTherapistId() + " - ")) { cmbTherapist.setValue(item); break; }
        }
        dtpSessionDate.setValue(dto.getSessionDate());
        cmbTimeSlot.setValue(dto.getTimeSlot());
    }
}
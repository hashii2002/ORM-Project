package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.serenitytherapycenterorm.bo.custom.PatientBO;
import org.example.serenitytherapycenterorm.bo.custom.ProgramBO;
import org.example.serenitytherapycenterorm.bo.custom.impl.PatientBOImpl;
import org.example.serenitytherapycenterorm.bo.custom.impl.ProgramBOImpl;
import org.example.serenitytherapycenterorm.dto.PatientDTO;
import org.example.serenitytherapycenterorm.dto.TherapyProgramDTO;
import org.example.serenitytherapycenterorm.exception.ValidationException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AddNewPatientController {

    @FXML private TextField txtPatientName;
    @FXML private DatePicker txtDOB;
    @FXML private TextField txtPatientPhone;
    @FXML private TextField txtPatientAddress;
    @FXML private TextArea txtInterviewNote;
    @FXML private ComboBox<String> cmbStatus;

    // Therapy Program Enrollment
    @FXML private ComboBox<TherapyProgramDTO> cmbPatientProgram;
    @FXML private Button btnAddPgm;

    // Program Table
    @FXML private TableView<TherapyProgramDTO> tblSelectedPgm;
    @FXML private TableColumn<TherapyProgramDTO, String> colProgramName;
    @FXML private TableColumn<TherapyProgramDTO, Integer> colTotalSessions;
    @FXML private TableColumn<TherapyProgramDTO, BigDecimal> colSubtotal;
    @FXML private TableColumn<TherapyProgramDTO, Void> colAction;

    // Payment Section
    @FXML private ComboBox<String> cmbPaymentMethod;
    @FXML private TextField txtDiscount;
    @FXML private TextField txtUpfrontPaid;

    // Bill Display Labels
    @FXML private Label lblSubtotal;
    @FXML private Label lblDiscount;
    @FXML private Label lblTotalDue;
    @FXML private Label lblUpfrontPaidDisplay;
    @FXML private Label lblRemainingBalance;
    @FXML private Label lblRegMessage;

    private final PatientBO patientBO = new PatientBOImpl();
    private final ProgramBO programBO = new ProgramBOImpl();

    private final ObservableList<TherapyProgramDTO> selectedProgramList = FXCollections.observableArrayList();
    private PatientController parentController;

    public void setParentController(PatientController parentController) {
        this.parentController = parentController;
    }

    public void initialize() {
        // Setup Table Columns
        colProgramName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTotalSessions.setCellValueFactory(new PropertyValueFactory<>("totalSessions"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("fee"));

        tblSelectedPgm.setItems(selectedProgramList);
        initTableActionColumn(); // Set Delete Button

        cmbStatus.setItems(FXCollections.observableArrayList("Active", "Inactive", "Completed"));
        cmbStatus.getSelectionModel().selectFirst();

        //Load Programs to ComboBox & Format display text
        loadAllProgramsToCombo();
        initPaymentMethodsCombo();

        // Add Real-time Calculation Listeners
        txtDiscount.textProperty().addListener((observable, oldValue, newValue) -> calculateBill());
        txtUpfrontPaid.textProperty().addListener((observable, oldValue, newValue) -> calculateBill());
    }

    private void loadAllProgramsToCombo() {
        try {
            List<TherapyProgramDTO> allPrograms = programBO.getAllPrograms();
            cmbPatientProgram.setItems(FXCollections.observableArrayList(allPrograms));

            cmbPatientProgram.setConverter(new StringConverter<TherapyProgramDTO>() {
                @Override
                public String toString(TherapyProgramDTO program) {
                    return program == null ? "" : program.getName() + " (LKR " + program.getFee() + ")";
                }
                @Override
                public TherapyProgramDTO fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPaymentMethodsCombo() {
        cmbPaymentMethod.setItems(FXCollections.observableArrayList("Cash", "Card", "Bank Transfer"));
    }

   // ADD Button Click Logic
    @FXML
    void handleAddProgramToTable(ActionEvent event) {
        TherapyProgramDTO selectedProgram = cmbPatientProgram.getSelectionModel().getSelectedItem();

        if (selectedProgram == null) {
            lblRegMessage.setText("Please select a therapy program first!");
            return;
        }

        // Duplicate Check
        for (TherapyProgramDTO pgm : selectedProgramList) {
            if (pgm.getId().equals(selectedProgram.getId())) {
                lblRegMessage.setText("This program is already added to the selection!");
                return;
            }
        }

        selectedProgramList.add(selectedProgram);
        lblRegMessage.setText("");
        calculateBill();
    }

    private void initTableActionColumn() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Remove");
            {
                btnDelete.setStyle("-fx-background-color: #d45b55; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 11px;");
                btnDelete.setOnAction(event -> {
                    TherapyProgramDTO pgm = getTableView().getItems().get(getIndex());
                    selectedProgramList.remove(pgm);
                    calculateBill();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null);
                else setGraphic(btnDelete);
            }
        });
    }

    private void calculateBill() {
        BigDecimal subtotal = BigDecimal.ZERO;

        // Calculation of all program fees in the table
        for (TherapyProgramDTO pgm : selectedProgramList) {
            if (pgm.getFee() != null) {
                subtotal = subtotal.add(pgm.getFee());
            }
        }
        lblSubtotal.setText(subtotal.setScale(2, RoundingMode.HALF_UP) + " LKR");

        // Reducing the Discount
        BigDecimal discount = BigDecimal.ZERO;
        try {
            String discText = txtDiscount.getText().trim();
            if (!discText.isEmpty()) {
                discount = new BigDecimal(discText);
            }
        } catch (NumberFormatException e) {
        }
        lblDiscount.setText("- " + discount.setScale(2, RoundingMode.HALF_UP) + " LKR");

        // 3. Total Program Fee
        BigDecimal totalProgramFee = subtotal.subtract(discount);
        if (totalProgramFee.compareTo(BigDecimal.ZERO) < 0) {
            totalProgramFee = BigDecimal.ZERO;
        }
        lblTotalDue.setText(totalProgramFee.setScale(2, RoundingMode.HALF_UP) + " LKR");

        // Display Upfront Paid Amount
        BigDecimal upfrontPaid = BigDecimal.ZERO;
        try {
            String upfrontText = txtUpfrontPaid.getText().trim();
            if (!upfrontText.isEmpty()) {
                upfrontPaid = new BigDecimal(upfrontText);
            }
        } catch (NumberFormatException e) {
        }
        lblUpfrontPaidDisplay.setText(upfrontPaid.setScale(2, RoundingMode.HALF_UP) + " LKR");

        // Calculate Remaining Balance
        BigDecimal remainingBalance = totalProgramFee.subtract(upfrontPaid);
        lblRemainingBalance.setText(remainingBalance.setScale(2, RoundingMode.HALF_UP) + " LKR");
    }

    @FXML
    void handleRegisterPatient(ActionEvent event) {
        try {
            String name = txtPatientName.getText();
            String phone = txtPatientPhone.getText();
            String address = txtPatientAddress.getText();
            String status = cmbStatus.getSelectionModel().getSelectedItem();
            String pMethod = cmbPaymentMethod.getSelectionModel().getSelectedItem();
            String upfrontStr = txtUpfrontPaid.getText();
            LocalDate dob = txtDOB.getValue();

            // Validation
            if (name == null || name.trim().isEmpty() || phone == null || phone.trim().isEmpty()) {
                throw new ValidationException("Please enter Full Name and Phone Number!");
            }
            if (selectedProgramList.isEmpty()) {
                throw new ValidationException("Please enroll the patient in at least one therapy program!");
            }
            if (pMethod == null) {
                throw new ValidationException("Please select a valid Payment Method!");
            }
            if (upfrontStr == null || upfrontStr.trim().isEmpty()) {
                throw new ValidationException("Please enter the Upfront Paid Amount!");
            }

            PatientDTO dto = new PatientDTO();
            dto.setName(name.trim());
            dto.setPhone(phone.trim());
            dto.setAddress(address.trim());
            dto.setRegisteredDate(LocalDate.now());
            dto.setInterviewNote(txtInterviewNote.getText() != null ? txtInterviewNote.getText() : "");
            dto.setDob(dob);
            dto.setStatus(status != null ? status : "Active");
            dto.setEmail("");

            dto.setPrograms(new ArrayList<>(selectedProgramList));

            boolean isSaved = patientBO.savePatient(dto);

            if (isSaved) {
                if (parentController != null) parentController.loadAllPatients();
                new Alert(Alert.AlertType.INFORMATION, "Patient Registered Successfully!").showAndWait();
                ((Stage) txtPatientName.getScene().getWindow()).close();
            } else {
                lblRegMessage.setText("Failed to save patient in Database!");
            }

        } catch (ValidationException e) {
            lblRegMessage.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            lblRegMessage.setText("Registration Error: " + e.getMessage());
        }
    }

    @FXML
    void handleClearPatientForm(ActionEvent event) {
        txtPatientName.clear();
        txtPatientPhone.clear();
        txtPatientAddress.clear();
        if (txtDOB != null) txtDOB.setValue(null);
        txtInterviewNote.clear();
        txtDiscount.clear();
        txtUpfrontPaid.clear();

        cmbPatientProgram.getSelectionModel().clearSelection();
        cmbPaymentMethod.getSelectionModel().clearSelection();
        cmbStatus.getSelectionModel().select("Active");
        selectedProgramList.clear();

        selectedProgramList.clear();
        calculateBill();
        lblRegMessage.setText("");
    }
}

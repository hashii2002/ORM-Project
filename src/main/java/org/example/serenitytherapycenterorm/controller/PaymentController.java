package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.PatientBO;
import org.example.serenitytherapycenterorm.bo.custom.PaymentBO;
import org.example.serenitytherapycenterorm.dto.PatientDTO;
import org.example.serenitytherapycenterorm.dto.PaymentDTO;
import org.example.serenitytherapycenterorm.exception.ValidationException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PaymentController {

    @FXML private Label lblTotalRevenue;
    @FXML private Label lblTodayTransactions;
    @FXML private ComboBox<String> cmbPatient;
    @FXML private TextField txtTotalFee;
    @FXML private TextField txtUpfrontAmount;
    @FXML private Label lblDueBalance;
    @FXML private TextField txtAmountPaid;
    @FXML private Label lblBalanceChange;
    @FXML private DatePicker dtpPaymentDate;
    @FXML private ComboBox<String> cmbPaymentMethod;

    @FXML private TextField txtSearchPayment;
    @FXML private ComboBox<String> cmbFilterMethodTable;
    @FXML private Button btnDeletePayment;

    @FXML private TableView<PaymentDTO> tblPayments;
    @FXML private TableColumn<PaymentDTO, Long> colPaymentId;
    @FXML private TableColumn<PaymentDTO, String> colPatientName;
    @FXML private TableColumn<PaymentDTO, Double> colAmount;
    @FXML private TableColumn<PaymentDTO, LocalDate> colDate;
    @FXML private TableColumn<PaymentDTO, String> colMethod;

    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PAYMENT);
    private final PatientBO patientBO = (PatientBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PATIENT);

    private final ObservableList<PaymentDTO> paymentList = FXCollections.observableArrayList();
    private FilteredList<PaymentDTO> filteredData;

    @FXML
    public void initialize() {
        cmbPaymentMethod.getItems().addAll("Cash", "Card", "Bank Transfer");
        cmbFilterMethodTable.getItems().addAll("All Methods", "Cash", "Card", "Bank Transfer");
        cmbFilterMethodTable.getSelectionModel().selectFirst(); // Default "All Methods"

        colPaymentId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("upfrontAmount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        filteredData = new FilteredList<>(paymentList, p -> true);
        tblPayments.setItems(filteredData);

        txtTotalFee.textProperty().addListener((obs, oldVal, newVal) -> calculateBalances());
        txtUpfrontAmount.textProperty().addListener((obs, oldVal, newVal) -> calculateBalances());
        txtAmountPaid.textProperty().addListener((obs, oldVal, newVal) -> calculateBalances());

        cmbFilterMethodTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            filterTableByMethod(newValue);
        });

        // Searchbar logic
        txtSearchPayment.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                searchAndSelectPaymentRow();
            }
        });

        btnDeletePayment.setOnAction(this::handleDeletePayment);

        loadAllPayments();
        loadAllPatientsToComboBox();
    }

    private void searchAndSelectPaymentRow() {
        String searchText = txtSearchPayment.getText().trim();
        if (searchText.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a Payment ID to search!").show();
            return;
        }

        boolean found = false;
        for (PaymentDTO payment : paymentList) {

            if (String.valueOf(payment.getPaymentId()).equals(searchText) ||
                    payment.getPatientName().equalsIgnoreCase(searchText)) {

                tblPayments.getSelectionModel().select(payment);
                tblPayments.scrollTo(payment);
                found = true;
                break;
            }
        }

        if (!found) {
            new Alert(Alert.AlertType.INFORMATION, "No payment found with ID/Name: " + searchText).show();
        }
    }

    @FXML
    void handleDeletePayment(ActionEvent event) {
        PaymentDTO selectedPayment = tblPayments.getSelectionModel().getSelectedItem();

        if (selectedPayment == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a payment bill from the table to delete!").show();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Payment Bill");
        alert.setHeaderText("Are you sure you want to delete this bill?");
        alert.setContentText("Payment ID: " + selectedPayment.getPaymentId() + "\nPatient: " + selectedPayment.getPatientName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {

                boolean isDeleted = paymentBO.deletePayment(selectedPayment.getPaymentId());
                if (isDeleted) {
                    new Alert(Alert.AlertType.INFORMATION, "Payment bill deleted successfully!").show();
                    loadAllPayments();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to delete the payment bill!").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "Error occurred while deleting: " + e.getMessage()).show();
                e.printStackTrace();
            }
        }
    }

    private void filterTableByMethod(String method) {
        filteredData.setPredicate(payment -> {
            if (method == null || method.equals("All Methods")) {
                return true;
            }

            return payment.getPaymentMethod().equalsIgnoreCase(method);
        });

        updateRevenueLabels(filteredData);
    }

    private void loadAllPayments() {
        try {
            paymentList.clear();
            List<PaymentDTO> allPayments = paymentBO.getAllPayments();
            paymentList.addAll(allPayments);

            updateRevenueLabels(paymentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateRevenueLabels(List<PaymentDTO> list) {
        double tot = 0;
        for (PaymentDTO d : list) tot += d.getUpfrontAmount();
        lblTotalRevenue.setText(String.format("%.2f", tot));
        lblTodayTransactions.setText(String.valueOf(list.size()));
    }

    private void calculateBalances() {
        try {
            double totalFee = txtTotalFee.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtTotalFee.getText().trim());
            double upfrontAmount = txtUpfrontAmount.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtUpfrontAmount.getText().trim());
            double amountPaid = txtAmountPaid.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtAmountPaid.getText().trim());

            double dueBalance = totalFee - upfrontAmount;
            lblDueBalance.setText(String.format("%.2f", dueBalance > 0 ? dueBalance : 0.00));

            double change = amountPaid - upfrontAmount;
            lblBalanceChange.setText(String.format("%.2f", change > 0 ? change : 0.00));
        } catch (NumberFormatException e) {
        }
    }

    @FXML
    void handleProcessPayment(ActionEvent event) {
        try {
            validateInputs();

            PaymentDTO dto = new PaymentDTO();
            String selectedPatientString = cmbPatient.getValue();

            if (selectedPatientString != null && selectedPatientString.contains(" - ")) {
                String[] parts = selectedPatientString.split(" - ");
                Long patientId = Long.parseLong(parts[0].trim());
                dto.setPatientId(patientId);
            } else {
                throw new ValidationException("Please select a valid patient from the list!");
            }

            dto.setTotalFee(Double.parseDouble(txtTotalFee.getText().trim()));
            dto.setUpfrontAmount(Double.parseDouble(txtUpfrontAmount.getText().trim()));
            dto.setAmountPaid(Double.parseDouble(txtAmountPaid.getText().trim()));
            dto.setPaymentDate(dtpPaymentDate.getValue());
            dto.setPaymentMethod(cmbPaymentMethod.getValue());

            if (paymentBO.savePayment(dto)) {
                new Alert(Alert.AlertType.INFORMATION, "Payment Processed successfully!").show();
                loadAllPayments();
                handleClearForm(null);
            }
        } catch (ValidationException e) {
            new Alert(Alert.AlertType.WARNING, e.getMessage()).show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }

    private void validateInputs() throws ValidationException {
        if (cmbPatient.getValue() == null) throw new ValidationException("Please select a registered patient!");
        String decimalRegex = "^[0-9]+(\\.[0-9]{1,2})?$";
        if (!txtTotalFee.getText().trim().matches(decimalRegex)) throw new ValidationException("Invalid Total Program Fee!");
        if (!txtUpfrontAmount.getText().trim().matches(decimalRegex)) throw new ValidationException("Invalid Upfront Amount!");
        if (!txtAmountPaid.getText().trim().matches(decimalRegex)) throw new ValidationException("Invalid Amount Paid!");

        double upfront = Double.parseDouble(txtUpfrontAmount.getText().trim());
        double paid = Double.parseDouble(txtAmountPaid.getText().trim());
        if (paid < upfront) throw new ValidationException("The cash received is less than upfront amount!");
        if (dtpPaymentDate.getValue() == null) throw new ValidationException("Please select the payment date!");
        if (cmbPaymentMethod.getValue() == null) throw new ValidationException("Please select a payment method!");
    }

    @FXML
    void handleClearForm(ActionEvent event) {
        cmbPatient.getSelectionModel().clearSelection();
        txtTotalFee.clear();
        txtUpfrontAmount.clear();
        txtAmountPaid.clear();
        lblDueBalance.setText("0.00");
        lblBalanceChange.setText("0.00");
        dtpPaymentDate.setValue(null);
        cmbPaymentMethod.getSelectionModel().clearSelection();
    }

    private void loadAllPatientsToComboBox() {
        try {
            List<PatientDTO> patientList = patientBO.getAllPatients();
            ObservableList<String> patientOptions = FXCollections.observableArrayList();
            for (PatientDTO patient : patientList) {
                patientOptions.add(patient.getId() + " - " + patient.getName());
            }
            cmbPatient.setItems(patientOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
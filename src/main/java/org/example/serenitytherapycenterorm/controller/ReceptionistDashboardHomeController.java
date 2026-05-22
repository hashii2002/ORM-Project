package org.example.serenitytherapycenterorm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.PatientBO;
import org.example.serenitytherapycenterorm.bo.custom.PaymentBO;
import org.example.serenitytherapycenterorm.bo.custom.TherapySessionBO;

import java.math.BigDecimal;

public class ReceptionistDashboardHomeController {

    @FXML private Label lblTotalPatients;
    @FXML private Label lblConfirmedAppointments;
    @FXML private Label lblTotalSessions;
    @FXML private Label lblTotalRevenue;

    private final PatientBO patientBO = (PatientBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PATIENT);
    private final TherapySessionBO sessionBO = (TherapySessionBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.SESSION);
    private final PaymentBO paymentBO = (PaymentBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.PAYMENT);

    @FXML
    public void initialize() {
        loadSummaryMetrics();
    }

    private void loadSummaryMetrics() {
        try {
            // Total Patients Count
            long totalPatients = patientBO.getPatientCount();
            lblTotalPatients.setText(String.valueOf(totalPatients));

            // Total Sessions Count
            long totalSessions = sessionBO.getTotalSessionCount();
            lblTotalSessions.setText(String.valueOf(totalSessions));

            // Confirmed Appointments Count
            long confirmedAppointments = sessionBO.getConfirmedAppointmentCount();
            lblConfirmedAppointments.setText(String.valueOf(confirmedAppointments));

            // Total Revenue
            BigDecimal totalRevenue = paymentBO.getTotalRevenue();
            lblTotalRevenue.setText(String.format("%,.2f", totalRevenue));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load summary details: " + e.getMessage());
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

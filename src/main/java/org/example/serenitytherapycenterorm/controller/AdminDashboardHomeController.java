package org.example.serenitytherapycenterorm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.serenitytherapycenterorm.bo.custom.AdminDashboardHomeBO;
import org.example.serenitytherapycenterorm.bo.custom.impl.AdminDashboardHomeBOImpl;

public class AdminDashboardHomeController {

    @FXML
    private Label lblTotalPrograms;

    @FXML
    private Label lblTotalRevenue;

    @FXML
    private Label lblTotalTherapists;

    @FXML
    private Label lblTotalUsers;

    private final AdminDashboardHomeBO dashboardBO = new AdminDashboardHomeBOImpl();

    @FXML
    public void initialize() {
        loadDashboardSummary();
    }

    private void loadDashboardSummary() {
        try {

            long totalUsers = dashboardBO.getTotalUsers();
            long totalTherapists = dashboardBO.getTotalTherapists();
            long totalPrograms = dashboardBO.getTotalPrograms();
            double totalRevenue = dashboardBO.getTotalRevenue();

            lblTotalUsers.setText(String.valueOf(totalUsers));
            lblTotalTherapists.setText(String.valueOf(totalTherapists));
            lblTotalPrograms.setText(String.valueOf(totalPrograms));

            lblTotalRevenue.setText(String.format("%,.2f", totalRevenue));

        } catch (Exception e) {
            e.printStackTrace();
            lblTotalUsers.setText("0");
            lblTotalTherapists.setText("0");
            lblTotalPrograms.setText("0");
            lblTotalRevenue.setText("0.00");
        }
    }
}
package org.example.serenitytherapycenterorm.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {

    @FXML private AnchorPane ancContent;
    @FXML private Label lblAdminName;

    @FXML
    public void initialize() {
        // Dashboard එක මුලින්ම Run වෙද්දී Default විදිහට පෙන්වන්න ඕන පිටුව මෙතන ලෝඩ් කරන්න පුළුවන්
        loadUI("/view/AdminDashboardHome.fxml"); // (ඔයා ළඟ දැනට තියෙන Home/Dashboard FXML path එක දාන්න)
    }

    @FXML
    void btnDashboardOnAction(ActionEvent event) {
        loadUI("/view/AdminDashboardHome.fxml");
    }

    @FXML
    void btnManageUsersOnAction(ActionEvent event) {
        loadUI("/view/User.fxml");
    }

    @FXML
    void btnManageTherapistsOnAction(ActionEvent event) {
        loadUI("/view/TherapistManagement.fxml");
    }

    @FXML
    void btnTherapyProgramsOnAction(ActionEvent event) {
        loadUI("/view/TherapyProgram.fxml");
    }

    @FXML
    void btnPaymentsOnAction(ActionEvent event) {
        loadUI("/view/Payment.fxml");
    }

    @FXML
    void btnReportsOnAction(ActionEvent event) {
        loadUI("/view/Report.fxml");
    }

    // 💡 1. පැත්තක තියෙන සුදු AnchorPane (ancContent) එකට පිටු Load කරන පොදු මෙතඩ් එක
    private void loadUI(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            ancContent.getChildren().clear(); // දැනට තියෙන UI එක අයින් කරනවා
            ancContent.getChildren().add(root); // අලුත් UI එක ඇතුළට දානවා
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Cannot load the page: " + fxmlPath);
        }
    }

    // 💡 2. Exit බටන් එක ක්ලික් කරාම මුළු Screen එකම ආපහු Login එකට හරවන මෙතඩ් එක
    @FXML
    void btnExitOnAction(ActionEvent event) {
        try {
            // දැනට තියෙන Dashboard Stage එක (Window) එක ගන්නවා
            Stage currentStage = (Stage) ancContent.getScene().getWindow();

            // Login FXML එක මුළුමනින්ම Load කරගන්නවා
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));

            // අලුත් Window එකක් අරින්නෙ නැතුව දැනට තියෙන Stage එකේ Scene එකටම Login UI එක සෙට් කරනවා
            Scene loginScene = new Scene(loginRoot, 1280, 800);
            currentStage.setScene(loginScene);
            currentStage.setTitle("Serenity Therapy Center - Login");
            currentStage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Cannot navigate back to Login Page.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
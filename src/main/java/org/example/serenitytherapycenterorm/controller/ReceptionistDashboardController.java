package org.example.serenitytherapycenterorm.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ReceptionistDashboardController {

    @FXML
    private AnchorPane ancContent;

    @FXML
    public void initialize() {
        loadUI("/view/ReceptionistDashboardHome.fxml");
    }

    @FXML
    void btnDashboardOnAction(ActionEvent event) {
        loadUI("/view/ReceptionistDashboardHome.fxml");
    }

    @FXML
    void btnManagePatientOnAction(ActionEvent event) {
        loadUI("/view/Patient.fxml");
    }

    @FXML
    void btnSessionOnAction(ActionEvent event) {
        loadUI("/view/TherapySessionScheduling.fxml");
    }

    @FXML
    void btnPaymentsOnAction(ActionEvent event) {
        loadUI("/view/Payment.fxml");
    }

    private void loadUI(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));

            ancContent.getChildren().clear();
            ancContent.getChildren().add(root);

            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Cannot load the page: " + fxmlPath);
        }
    }

    @FXML
    void btnExitOnAction(ActionEvent event) {
        try {
            Stage currentStage = (Stage) ancContent.getScene().getWindow();

            Parent loginRoot = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));

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

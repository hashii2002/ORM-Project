package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.UserBO;
import org.example.serenitytherapycenterorm.dto.UserDTO;
import org.example.serenitytherapycenterorm.entity.User;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private ChoiceBox<User.Role> cmbRole;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;

    private final UserBO userBO = (UserBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.USER);

    @FXML
    public void initialize() {
        cmbRole.setItems(FXCollections.observableArrayList(User.Role.values()));
    }

    @FXML
    void btnCreateAccount(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String address = txtAddress.getText().trim();
        User.Role role = cmbRole.getValue();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        // Validation
        if (username.isEmpty() || fullName.isEmpty() || email.isEmpty() || address.isEmpty() || role == null || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "All fields are required!");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters long!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match!");
            return;
        }

        try {

            UserDTO dto = new UserDTO(null, username, password, fullName, email, address, role, User.Status.ACTIVE);

            if (userBO.registerUser(dto)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account Created Successfully!");
                clearFields();
                navigateToLogin();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Error occurred or Username/Email already exists!");
        }
    }

    @FXML
    void linkLoginOnAction(ActionEvent event) {
        navigateToLogin();
    }

    private void navigateToLogin() {
        try {
            Stage currentStage = (Stage) txtUsername.getScene().getWindow();
            currentStage.close();

            Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root, 1280, 800));
            stage.setTitle("Serenity Therapy Center - Login");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot load Login UI.");
        }
    }

    private void clearFields() {
        txtUsername.clear();
        txtFullName.clear();
        txtEmail.clear();
        txtAddress.clear();
        cmbRole.setValue(null);
        txtPassword.clear();
        txtConfirmPassword.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
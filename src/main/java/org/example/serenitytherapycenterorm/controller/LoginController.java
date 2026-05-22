package org.example.serenitytherapycenterorm.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.UserBO;
import org.example.serenitytherapycenterorm.dto.UserDTO;
import org.example.serenitytherapycenterorm.entity.User;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private AnchorPane rootPane;
    @FXML private TextField txtPasswordVisible;
    @FXML private CheckBox chkShowPassword;

    private final UserBO userBO = (UserBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.USER);

    @FXML
    void chkShowPasswordOnAction(ActionEvent event) {
        if (chkShowPassword.isSelected()) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPassword.setVisible(false);
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPasswordVisible.setVisible(false);
        }
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // Validation
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter both Username and Password!");
            return;
        }

        try {
            UserDTO authenticatedUser = userBO.authenticate(username, password);

            if (authenticatedUser != null) {
                navigateToDashboard(authenticatedUser);
            }
        } catch (org.example.serenitytherapycenterorm.exception.AuthenticationException e) {
            showAlert(Alert.AlertType.ERROR, "Login Failed", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    // Dashboard Select for Role
    private void navigateToDashboard(UserDTO user) throws IOException {
        Stage currentStage = (Stage) txtUsername.getScene().getWindow();
        currentStage.close();

        String fxmlPath = "";
        String windowTitle = "";

        if (user.getRole() == User.Role.ADMIN) {
            fxmlPath = "/view/AdminDashboard.fxml";
            windowTitle = "Serenity Therapy Center - Admin Dashboard";
        } else if (user.getRole() == User.Role.RECEPTIONIST) {
            fxmlPath = "/view/ReceptionistDashboard.fxml";
            windowTitle = "Serenity Therapy Center - Receptionist Dashboard";
        } else {
            showAlert(Alert.AlertType.ERROR, "Access Denied", "Your account role is not recognized!");
            return;
        }

        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 1280, 800)); // Dashboard එක නිවැරදි ප්‍රමාණයෙන් විවෘත කිරීමට
        stage.setTitle(windowTitle);
        stage.centerOnScreen();
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    void btnCreateAccount(ActionEvent event) {
        try {
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/view/Register.fxml"));
            rootPane.getChildren().clear();
            rootPane.getChildren().add(registerRoot);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Cannot load Register UI. Please check the FXML path.");
        }
    }
}

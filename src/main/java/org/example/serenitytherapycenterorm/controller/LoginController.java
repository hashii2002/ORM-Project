package org.example.serenitytherapycenterorm.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.UserBO;
import org.example.serenitytherapycenterorm.entity.User;

import java.io.IOException;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword; // Course work එකේ Password පේන්න හදන එක පසුව කරමු
    @FXML private AnchorPane rootPane;

    // BO Layer එක සම්බන්ධ කරගැනීම
    private final UserBO userBO = (UserBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.USER);

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        // 1. Validation (හිස්තැන් පරීක්ෂාව)
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter both Username and Password!");
            return;
        }

        try {
            User authenticatedUser = userBO.authenticate(username, password);
            if (authenticatedUser != null) {
                showAlert(Alert.AlertType.INFORMATION, "Login Success", "Welcome, " + authenticatedUser.getFullName());
                navigateToDashboard();
            }
        } catch (org.example.serenitytherapycenterorm.exception.AuthenticationException e) {
            // 💡 මෙතනදී අපේ Custom Exception එක අල්ලාගෙන, එහි ඇති නිවැරදි Message එක Alert එකට දමයි
            showAlert(Alert.AlertType.ERROR, "Login Failed", e.getMessage());
        } catch (Exception e) {
            // වෙනත් ඩේටාබේස් හෝ සර්වර් දෝෂ සඳහා පොදු Alert එක
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "An unexpected error occurred.");
        }
    }

    private void navigateToDashboard() throws IOException {
        // දැනට තියෙන Login Window එක Close කිරීම
        Stage currentStage = (Stage) txtUsername.getScene().getWindow();
        currentStage.close();

        // Dashboard Window එක Open කිරීම
        Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml")); // ඔයාගේ Dashboard FXML path එක දාන්න
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Serenity Therapy Center - Dashboard");
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
            // 1. Register.fxml ෆයිල් එක Load කරගන්නවා
            Parent registerRoot = FXMLLoader.load(getClass().getResource("/view/Register.fxml"));

            // 2. White AnchorPane එක ඇතුළේ දැනට තියෙන දේවල් (Login Fields) සම්පූර්ණයෙන්ම ඉවත් කරනවා
            rootPane.getChildren().clear();

            // 3. අලුත් Register UI එක ඒ White AnchorPane එක ඇතුළට එකතු කරනවා
            rootPane.getChildren().add(registerRoot);

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error", "Cannot load Register UI. Please check the FXML path.");
        }
    }
}

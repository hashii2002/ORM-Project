package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.UserBO;
import org.example.serenitytherapycenterorm.dto.UserDTO;
import org.example.serenitytherapycenterorm.entity.User;
import org.example.serenitytherapycenterorm.exception.UiException;

public class AddNewUserController {

    @FXML private TextField txtFullName;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;
    @FXML private ComboBox<User.Role> cmbRole;
    @FXML private ComboBox<User.Status> cmbStatus;

    private final UserBO userBO = (UserBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.USER);

    @FXML
    public void initialize() {

        if (cmbRole != null) {
            cmbRole.setItems(FXCollections.observableArrayList(User.Role.values()));
        }
        if (cmbStatus != null) {
            cmbStatus.setItems(FXCollections.observableArrayList(User.Status.values()));
        }

        clearFields();
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        try {

            String fullName = txtFullName.getText().trim();
            String username = txtUsername.getText().trim();
            String email = txtEmail.getText().trim();
            String address = txtAddress.getText().trim();
            User.Role role = cmbRole.getValue();
            User.Status status = cmbStatus.getValue();

            // Data Validation
            if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || role == null || status == null) {
                throw new UiException("Please fill all mandatory fields before saving!");
            }

            UserDTO userDTO = new UserDTO();
            userDTO.setFullName(fullName);
            userDTO.setUsername(username);
            userDTO.setPassword(null);
            userDTO.setEmail(email);
            userDTO.setAddress(address);
            userDTO.setRole(role);
            userDTO.setStatus(status);

            boolean isSaved = userBO.saveUser(userDTO);

            if (isSaved) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User Registered Successfully!");
                clearFields();

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Failed to save the user. Please try again.");
            }

        } catch (UiException e) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "An error occurred while saving: " + e.getMessage());
        }
    }

    @FXML
    void btnCancelOnAction(ActionEvent event) {
        clearFields();
    }

    private void clearFields() {
        if (txtFullName != null) txtFullName.clear();
        if (txtUsername != null) txtUsername.clear();
        if (txtEmail != null) txtEmail.clear();
        if (txtAddress != null) txtAddress.clear();

        if (cmbRole != null) {
            cmbRole.getSelectionModel().clearSelection();
            cmbRole.setValue(null);
        }
        if (cmbStatus != null) {
            cmbStatus.getSelectionModel().clearSelection();
            cmbStatus.setValue(null);
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

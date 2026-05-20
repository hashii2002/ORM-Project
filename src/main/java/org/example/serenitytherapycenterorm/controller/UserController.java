package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.UserBO;
import org.example.serenitytherapycenterorm.dto.UserDTO;
import org.example.serenitytherapycenterorm.entity.User;
import org.example.serenitytherapycenterorm.exception.UiException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class UserController {
    @FXML private TableView<UserDTO> tblUser;
    @FXML private TableColumn<UserDTO, Long> colId;
    @FXML
    private TableColumn<UserDTO, String> colFullName;
    @FXML private TableColumn<UserDTO, String> colUsername;
    @FXML private TableColumn<UserDTO, User.Role> colRole;
    @FXML private TableColumn<UserDTO, String> colEmail;
    @FXML private TableColumn<UserDTO, String> colAddress;
    @FXML private TableColumn<UserDTO, User.Status> colStatus;
    @FXML private TableColumn<UserDTO, Void> colActions;

    private final UserBO userBO = (UserBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.USER);

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        addButtonToTable();
        loadAllUsers();
    }

    private void loadAllUsers() {
        try {
            List<UserDTO> allUsers = userBO.getAllUsers();
            ObservableList<UserDTO> dtoObservableList = FXCollections.observableArrayList(allUsers);
            tblUser.setItems(dtoObservableList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load users from the database.");
        }
    }

    private void addButtonToTable() {
        Callback<TableColumn<UserDTO, Void>, TableCell<UserDTO, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<UserDTO, Void> call(final TableColumn<UserDTO, Void> param) {
                final TableCell<UserDTO, Void> cell = new TableCell<>() {

                    private final Button btnDelete = new Button("Delete");

                    {
                        btnDelete.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

                        btnDelete.setOnAction(event -> {
                            UserDTO dto = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + dto.getUsername() + "?", ButtonType.YES, ButtonType.NO);
                            Optional<ButtonType> result = alert.showAndWait();

                            if (result.isPresent() && result.get() == ButtonType.YES) {
                                try {
                                    if (userBO.deleteUser(dto.getId())) {
                                        showAlert(Alert.AlertType.INFORMATION, "Success", "User Deleted Successfully!");
                                        loadAllUsers();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    showAlert(Alert.AlertType.ERROR, "Error", "Could not delete user.");
                                }
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnDelete);
                        }
                    }
                };
                return cell;
            }
        };

        colActions.setCellFactory(cellFactory);
    }

    @FXML
    void btnAddNewUserOnAction(ActionEvent event) {
        try {
            java.net.URL resource = getClass().getResource("/view/AddNewUser.fxml");
            if (resource == null) {
                throw new UiException("FXML file not found at path: /view/AddNewUser.fxml");
            }

            Parent root = FXMLLoader.load(resource);

            // Create Pop-Up Window
            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Serenity Therapy Center - Add New User");

            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(tblUser.getScene().getWindow());

            popupStage.setResizable(false);
            popupStage.showAndWait();

            loadAllUsers();

        } catch (UiException e) {
            showAlert(Alert.AlertType.ERROR, "UI Error", e.getMessage());
        } catch (IOException e) {
            UiException wrappedException = new UiException("Failed to open Add New User window", e);
            wrappedException.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", wrappedException.getMessage());
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

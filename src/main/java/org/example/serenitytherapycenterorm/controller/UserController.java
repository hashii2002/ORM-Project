package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.UserBO;
import org.example.serenitytherapycenterorm.dto.UserDTO;
import org.example.serenitytherapycenterorm.entity.User;

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
        // 1. සාමාන්‍ය Columns වලට UserDTO එකේ තියෙන Field Names Map කිරීම
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // 2. Action Column එකට TM ක්ලාස් එකක් නැතුව බටන් එකක් එකතු කිරීම
        addButtonToTable();

        // 3. දත්ත Load කිරීම
        loadAllUsers();
    }

    private void loadAllUsers() {
        try {
            // BO Layer එකෙන් කෙලින්ම එන UserDTO List එක Table එකට දමයි
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
                        // බටන් එකේ Style එක ලස්සන කරගැනීම
                        btnDelete.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");

                        // බටන් එක Click කරද්දී සිදුවන Action එක
                        btnDelete.setOnAction(event -> {
                            // ක්ලික් කරපු පේළියට අදාළ UserDTO ඔබ්ජෙක්ට් එක ගැනීම
                            UserDTO dto = getTableView().getItems().get(getIndex());

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete " + dto.getUsername() + "?", ButtonType.YES, ButtonType.NO);
                            Optional<ButtonType> result = alert.showAndWait();

                            if (result.isPresent() && result.get() == ButtonType.YES) {
                                try {
                                    if (userBO.deleteUser(dto.getId())) {
                                        showAlert(Alert.AlertType.INFORMATION, "Success", "User Deleted Successfully!");
                                        loadAllUsers(); // ටේබල් එක රිප්‍රෙෂ් කිරීම
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
                            setGraphic(btnDelete); // පේළිය හිස් නැත්නම් බටන් එක පෙන්වයි
                        }
                    }
                };
                return cell;
            }
        };

        colActions.setCellFactory(cellFactory);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

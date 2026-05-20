package org.example.serenitytherapycenterorm.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.serenitytherapycenterorm.bo.BOFactory;
import org.example.serenitytherapycenterorm.bo.custom.UserBO;
import org.example.serenitytherapycenterorm.dto.UserDTO;
import org.example.serenitytherapycenterorm.entity.User;
import org.example.serenitytherapycenterorm.exception.UiException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserController {
    @FXML private Label lblTotalUsers;
    @FXML private Label lblActiveUsers;
    @FXML private Label lblInactiveUsers;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbRoleSearch;
    @FXML private ComboBox<String> cmbStatusSearch;

    @FXML private TableView<UserDTO> tblUser;
    @FXML private TableColumn<UserDTO, Long> colId;
    @FXML private TableColumn<UserDTO, String> colFullName;
    @FXML private TableColumn<UserDTO, String> colUsername;
    @FXML private TableColumn<UserDTO, User.Role> colRole;
    @FXML private TableColumn<UserDTO, String> colEmail;
    @FXML private TableColumn<UserDTO, String> colAddress;
    @FXML private TableColumn<UserDTO, User.Status> colStatus;
    @FXML private TableColumn<UserDTO, Void> colActions;

    private final UserBO userBO = (UserBO) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.USER);
    private ObservableList<UserDTO> userList = FXCollections.observableArrayList();
    private ObservableList<UserDTO> masterUserDataList = FXCollections.observableArrayList();
    private FilteredList<UserDTO> filteredUserDataList;

    @FXML
    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Editable Table
        tblUser.setEditable(true);

        colFullName.setCellFactory(TextFieldTableCell.forTableColumn());
        colUsername.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colAddress.setCellFactory(TextFieldTableCell.forTableColumn());

        setupEditCommitHandlers();
        addButtonToTable();
        loadAllUsers();
        setupSearchLogic();
        loadFilterComboBoxData();
        setupComboBoxFilterLogic();
    }

    private void loadAllUsers() {
        try {
            List<UserDTO> allUsers = userBO.getAllUsers();

            masterUserDataList.clear();
            masterUserDataList.addAll(allUsers);
            filteredUserDataList = new FilteredList<>(masterUserDataList, p -> true);

            tblUser.setItems(filteredUserDataList);

            // Total user, Active user, Inactive user count (Summary Cards)
            int total = allUsers.size();

            long activeCount = allUsers.stream()
                    .filter(user -> User.Status.ACTIVE.equals(user.getStatus()))
                    .count();

            long inactiveCount = allUsers.stream()
                    .filter(user -> User.Status.INACTIVE.equals(user.getStatus()))
                    .count();

            // Update label counts
            lblTotalUsers.setText(String.valueOf(total));
            lblActiveUsers.setText(String.valueOf(activeCount));
            lblInactiveUsers.setText(String.valueOf(inactiveCount));

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

    private void setupSearchLogic() {
        txtSearch.setOnAction(event -> {
            String searchText = txtSearch.getText().trim();

            if (searchText.isEmpty()) {
                loadAllUsers();
                tblUser.getSelectionModel().clearSelection();
                return;
            }

            try {
                if (searchText.matches("\\d+")) {
                    Long id = Long.parseLong(searchText);
                    UserDTO user = userBO.searchUserById(id);

                    if (user != null) {
                        loadAllUsers();

                        for (UserDTO u : tblUser.getItems()) {
                            if (u.getId().equals(id)) {
                                tblUser.getSelectionModel().select(u);
                                tblUser.scrollTo(u);
                                break;
                            }
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Not Found", "User ID not found!");
                    }

                } else {
                    List<UserDTO> results = userBO.searchUserByFullName(searchText);

                    if (!results.isEmpty()) {
                        loadAllUsers();

                        String firstMatchName = results.get(0).getFullName().toLowerCase();

                        for (UserDTO u : tblUser.getItems()) {
                            if (u.getFullName().toLowerCase().contains(firstMatchName)) {
                                tblUser.getSelectionModel().select(u);
                                tblUser.scrollTo(u);
                                break;
                            }
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Not Found", "User Name not found!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Search failed: " + e.getMessage());
            }
        });
    }

    // Updates the DB when Enter is pressed after inline editing
    private void setupEditCommitHandlers() {

        colFullName.setOnEditCommit(event -> {
            UserDTO dto = event.getRowValue();
            dto.setFullName(event.getNewValue());
            updateUserInDatabase(dto);
        });

        colUsername.setOnEditCommit(event -> {
            UserDTO dto = event.getRowValue();
            dto.setUsername(event.getNewValue());
            updateUserInDatabase(dto);
        });

        colEmail.setOnEditCommit(event -> {
            UserDTO dto = event.getRowValue();
            dto.setEmail(event.getNewValue());
            updateUserInDatabase(dto);
        });

        colAddress.setOnEditCommit(event -> {
            UserDTO dto = event.getRowValue();
            dto.setAddress(event.getNewValue());
            updateUserInDatabase(dto);
        });
    }

    private void updateUserInDatabase(UserDTO dto) {
        try {
            boolean isUpdated = userBO.updateUser(dto);
            if (isUpdated) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User details updated successfully!");
                loadAllUsers();
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed", "Failed to update user.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "System Error", "Error updating data: " + e.getMessage());
        }
    }

    private void loadFilterComboBoxData() {
        List<String> roles = new ArrayList<>();
        roles.add("All Roles");
        for (User.Role r : User.Role.values()) {
            roles.add(r.name());
        }
        cmbRoleSearch.setItems(FXCollections.observableArrayList(roles));
        cmbRoleSearch.getSelectionModel().selectFirst();

        List<String> statuses = new ArrayList<>();
        statuses.add("All Status");
        for (User.Status s : User.Status.values()) {
            statuses.add(s.name());
        }
        cmbStatusSearch.setItems(FXCollections.observableArrayList(statuses));
        cmbStatusSearch.getSelectionModel().selectFirst();
    }

    // ComboBox Filter Logic
    private void setupComboBoxFilterLogic() {
        cmbRoleSearch.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            applyCombinedFilter();
        });

        cmbStatusSearch.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            applyCombinedFilter();
        });
    }

    private void applyCombinedFilter() {
        if (filteredUserDataList == null) {
            return;
        }

        String selectedRole = cmbRoleSearch.getValue();
        String selectedStatus = cmbStatusSearch.getValue();

        filteredUserDataList.setPredicate(userDTO -> {

            boolean roleMatch = (selectedRole == null || selectedRole.equals("All Roles") ||
                    userDTO.getRole().name().equals(selectedRole));

            boolean statusMatch = (selectedStatus == null || selectedStatus.equals("All Status") ||
                    userDTO.getStatus().name().equals(selectedStatus));

            return roleMatch && statusMatch;
        });
    }
}

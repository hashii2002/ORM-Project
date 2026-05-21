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
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.serenitytherapycenterorm.bo.custom.PatientBO;
import org.example.serenitytherapycenterorm.bo.custom.impl.PatientBOImpl;
import org.example.serenitytherapycenterorm.dto.PatientDTO;

import java.io.IOException;
import java.util.List;

public class PatientController {

    @FXML private Button btnAddNewPatient;
    @FXML private TableView<PatientDTO> tblPatient;
    @FXML private TableColumn<PatientDTO, Long> colId;
    @FXML private TableColumn<PatientDTO, String> colName;
    @FXML private TableColumn<PatientDTO, String> colContact;
    @FXML private TableColumn<PatientDTO, String> colDOB;
    @FXML private TableColumn<PatientDTO, String> colRegDate;
    @FXML private TableColumn<PatientDTO, String> colPrograms;
    @FXML private TableColumn<PatientDTO, String> colStatus;

    private final PatientBO patientBO = new PatientBOImpl();
    private final ObservableList<PatientDTO> patientList = FXCollections.observableArrayList();

    public void initialize() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colDOB.setCellValueFactory(new PropertyValueFactory<>("email"));
        colRegDate.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPrograms.setCellValueFactory(new PropertyValueFactory<>("programsDisplay"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("interviewNote"));

        loadAllPatients();
    }

    public void loadAllPatients() {
        patientList.clear();
        try {
            List<PatientDTO> allPatients = patientBO.getAllPatients();
            patientList.addAll(allPatients);
            tblPatient.setItems(patientList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddNewPatientClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/AddNewPatient.fxml"));
            Parent root = loader.load();

            AddNewPatientController addNewPatientController = loader.getController();
            addNewPatientController.setParentController(this);

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.initOwner(currentStage);
            popupStage.initStyle(StageStyle.DECORATED);
            popupStage.setTitle("Serenity Therapy Center - Patient Registration");

            BoxBlur blur = new BoxBlur(3, 3, 3);
            currentStage.getScene().getRoot().setEffect(blur);
            popupStage.setOnHidden(e -> currentStage.getScene().getRoot().setEffect(null));

            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);
            popupStage.setHeight(800);
            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

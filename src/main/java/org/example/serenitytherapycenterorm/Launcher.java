package org.example.serenitytherapycenterorm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.example.serenitytherapycenterorm.config.FactoryConfiguration;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Initialize Hibernate on startup
        FactoryConfiguration.getInstance();
        System.out.println("Hibernate initialized successfully");

        // Load Login Fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 1280,800);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

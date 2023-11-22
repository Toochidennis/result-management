package com.toochi.resultmanagement.controllers;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class LauncherController implements Initializable {

    @FXML
    private AnchorPane anchorPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        long splashDuration = 1000L;
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(splashDuration));
        pauseTransition.setOnFinished(event -> {
            Stage stage = (Stage) anchorPane.getScene().getWindow();
            stage.close();

            loadMainDashboard();
        });

        pauseTransition.play();
    }


    private void loadMainDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/toochi/resultmanagement/dashboard/dashboard_view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());

            stage.setTitle("Result management");
            stage.getIcons().add(new Image("/com/toochi/resultmanagement/assets/icons/logo.png"));
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
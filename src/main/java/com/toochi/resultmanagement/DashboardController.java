package com.toochi.resultmanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    Util util = new Util("Na me dey here");

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText(util.getMessage());
    }
}
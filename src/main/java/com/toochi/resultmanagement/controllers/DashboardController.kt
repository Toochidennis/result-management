package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.utils.Util.loadFXMLScene
import javafx.fxml.FXML
import javafx.scene.layout.BorderPane

class DashboardController {

    @FXML
    private lateinit var borderPane: BorderPane

    @FXML
    fun studentBtn() {
        loadFXMLScene(borderPane, "student/student_view.fxml")
    }

    @FXML
    fun resultBtn() {
        loadFXMLScene(borderPane, "result/result_input_view.fxml")
    }

    @FXML
    fun settingsBtn() {
        loadFXMLScene(borderPane, "settings/settings_view.fxml")
    }


    @FXML
    fun initialize() {
        loadFXMLScene(borderPane, "student/student_view.fxml")
    }

}

package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.utils.Util.loadFXMLScene
import javafx.fxml.FXML
import javafx.scene.layout.BorderPane

class DashboardController {

    @FXML
    private lateinit var borderPane: BorderPane


    @FXML
    fun studentBtn() {
        loadFXMLScene(borderPane, "student/add_student.fxml")
    }

    @FXML
    fun viewStudentBtn() {
        loadFXMLScene(borderPane, "student/view_student.fxml")
    }

    @FXML
    fun inputResultBtn() {
        loadFXMLScene(borderPane, "result/input_result.fxml")
    }

    @FXML
    fun viewResultBtn() {
        loadFXMLScene(borderPane, "result/view_student_result.fxml")
    }

    @FXML
    fun settingsBtn() {
        loadFXMLScene(borderPane, "settings/settings_view.fxml")
    }


    @FXML
    fun initialize() {
        loadFXMLScene(borderPane, "student/add_student.fxml")
    }


}

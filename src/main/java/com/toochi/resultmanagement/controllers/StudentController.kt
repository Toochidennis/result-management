package com.toochi.resultmanagement.controllers

import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent

class StudentController {

    @FXML
    private lateinit var addressTxt: TextField

    @FXML
    private lateinit var dateOfBirthPicker: DatePicker

    @FXML
    private lateinit var disciplineTxt: TextField

    @FXML
    private lateinit var emailTxt: TextField

    @FXML
    private lateinit var firstNameTxt: TextField

    @FXML
    private lateinit var lastNameTxt: TextField

    @FXML
    private lateinit var matricNumberTxt: TextField

    @FXML
    private lateinit var middleNameTxt: TextField

    @FXML
    private lateinit var phoneNumberTxt: TextField

    @FXML
    private lateinit var semesterCombo: ComboBox<Any>

    @FXML
    private lateinit var sessionCombo: ComboBox<Any>

    @FXML
    fun semesterClick(event: MouseEvent) {

    }

    @FXML
    fun sessionClick(event: MouseEvent) {

    }

}

package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor.executeInsertQuery
import com.toochi.resultmanagement.utils.Util.generateProgrammes
import com.toochi.resultmanagement.utils.Util.generateSessions
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField

class StudentController {


    @FXML
    private lateinit var firstNameTextField: TextField

    @FXML
    private lateinit var middleNameTextField: TextField

    @FXML
    private lateinit var lastNameTextField: TextField

    @FXML
    private lateinit var matricNumberTextField: TextField

    @FXML
    private lateinit var phoneNumberTextField: TextField

    @FXML
    private lateinit var disciplineTextField: TextField

    @FXML
    private lateinit var sessionComboBox: ComboBox<String>

    @FXML
    private lateinit var programmeComboBox: ComboBox<String>

    @FXML
    private lateinit var dateOfBirthPicker: DatePicker

    @FXML
    private lateinit var emailTextField: TextField

    @FXML
    private lateinit var addressTextField: TextField

    @FXML
    fun initialize() {
        createProgrammes()
        createSessions()
    }

    @FXML
    fun submitBtn(){
        getTextFromViews()
    }

    private fun createProgrammes() {
        programmeComboBox.items = generateProgrammes()
    }

    private fun createSessions() {
        sessionComboBox.items = generateSessions()
    }

    private fun getTextFromViews() {
        val firstName = firstNameTextField.text
        val middleName = middleNameTextField.text
        val lastName = lastNameTextField.text
        val matricNumber = matricNumberTextField.text
        val phoneNumber = phoneNumberTextField.text
        val discipline = disciplineTextField.text
        val session = sessionComboBox.value
        val programme = programmeComboBox.value
        val dateOfBirth = dateOfBirthPicker.value
        val email = emailTextField.text
        val address = addressTextField.text

        val values = HashMap<String, Any>().apply {
            put("first_name", firstName)
            put("middle_name", middleName)
            put("last_name", lastName)
            put("matric_number", matricNumber)
            put("phone_number", phoneNumber)
            put("discipline", discipline)
            put("session", session)
            put("programme", programme)
            put("date_of_birth", "$dateOfBirth")
            put("email", email)
            put("address", address)
        }

        executeInsertQuery("students", values)
    }

}

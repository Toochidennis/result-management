package com.toochi.resultmanagement.controllers

import com.jfoenix.controls.JFXButton
import com.toochi.resultmanagement.backend.QueryExecutor.executeInsertQuery
import com.toochi.resultmanagement.utils.Util.generateGender
import com.toochi.resultmanagement.utils.Util.generateProgrammes
import com.toochi.resultmanagement.utils.Util.generateSessions
import com.toochi.resultmanagement.utils.Util.openFileChooser
import com.toochi.resultmanagement.utils.Util.showMessageDialog
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.stage.FileChooser
import java.io.File

class StudentController {


    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var rootPane: StackPane

    @FXML
    private lateinit var firstNameTextField: TextField

    @FXML
    private lateinit var otherNameTextField: TextField

    @FXML
    private lateinit var surnameTextField: TextField

    @FXML
    private lateinit var registrationNumberTextField: TextField

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
    private lateinit var passportTextField: TextField

    @FXML
    private lateinit var genderComboBox: ComboBox<String>

    @FXML
    private lateinit var nextOfKinAddressTextField: TextField

    @FXML
    private lateinit var nextOfKinNameTextField: TextField

    @FXML
    private lateinit var nextOfKinPhoneNumberTextField: TextField

    @FXML
    private lateinit var supervisorTextField: TextField

    private var selectedFile: File? = null


    @FXML
    fun initialize() {
        createProgrammes()
        createSessions()
        createGender()

    }

    @FXML
    fun submitBtn() {
        validateInput
    }

    @FXML
    fun uploadImageBtn() {
        val filterExtension = FileChooser.ExtensionFilter(
            "Image Files",
            "*.png", "*.jpg", "*.jpeg", "*.gif"
        )
        selectedFile =
            openFileChooser(rootPane.scene.window, filterExtension, "Pictures", "Choose Image file")

        if (selectedFile != null) {
            val maxSize = 1024L
            val fileSize = selectedFile?.length()?.div(1024)
            val button = JFXButton("Okay")
            val header = "Error!"
            val body = "File size must not be greater than 1MB!"

            fileSize?.let {
                if (it > maxSize) {
                    showMessageDialog(rootPane, anchorPane, listOf(button), header, body)
                    selectedFile = null
                } else {
                    passportTextField.text = selectedFile?.name
                    showMessageDialog(
                        rootPane,
                        anchorPane,
                        listOf(button),
                        "Success",
                        "File uploaded!"
                    )
                }
            }
        }
    }


    private fun createProgrammes() {
        programmeComboBox.items = generateProgrammes()
    }

    private fun createSessions() {
        sessionComboBox.items = generateSessions()
    }

    private fun createGender() {
        genderComboBox.items = generateGender()
    }

    private fun processSubmissionForm() {
        // Personal data
        val surname = surnameTextField.text
        val firstName = firstNameTextField.text
        val otherName = otherNameTextField.text
        val registrationNumber = registrationNumberTextField.text
        val phoneNumber = phoneNumberTextField.text
        val dateOfBirth = dateOfBirthPicker.value
        val email = emailTextField.text
        val address = addressTextField.text
        val gender = genderComboBox.value
        val passport = selectedFile?.toPath() ?: ""

        // Next of kin data
        val nextOfKinName = nextOfKinNameTextField.text
        val nextOfKinAddress = nextOfKinAddressTextField.text
        val nextOfKinPhoneNumber = nextOfKinPhoneNumberTextField.text

        // Professional data
        val programme = programmeComboBox.value
        val discipline = disciplineTextField.text
        val session = sessionComboBox.value
        val supervisor = supervisorTextField.text

        val values = hashMapOf<String, Any>(
            "surname" to surname,
            "first_name" to firstName,
            "other_name" to otherName,
            "registration_number" to registrationNumber,
            "phone_number" to phoneNumber,
            "date_of_birth" to "$dateOfBirth",
            "email" to email,
            "address" to address,
            "gender" to gender,
            "passport" to passport,
            "next_of_kin_name" to nextOfKinName,
            "next_of_kin_phone_number" to nextOfKinPhoneNumber,
            "next_of_kin_address" to nextOfKinAddress,
            "session" to session,
            "programme" to programme,
            "discipline" to discipline,
            "supervisor" to supervisor
        )

        val affectedRows = executeInsertQuery("students", values)

        if (affectedRows == 1) {
            showMessageDialog(
                rootPane,
                anchorPane,
                listOf(JFXButton("Okay")),
                "Success",
                "Student registered successfully!"
            )
            clearFields()
        } else {
            showMessageDialog(
                rootPane,
                anchorPane,
                listOf(JFXButton("Okay, I'll check")),
                "Error!",
                "Oops! Something went wrong, please try again"
            )
        }
    }

    private val validateInput: Unit
        get() {
            when {
                surnameTextField.text.isBlank() -> {
                    showValidationMessage("Please provide surname")
                }

                firstNameTextField.text.isBlank() -> {
                    showValidationMessage("Please provide firstname")
                }

                registrationNumberTextField.text.isBlank() -> {
                    showValidationMessage("Please provide registration number")
                }

                phoneNumberTextField.text.isBlank() -> {
                    showValidationMessage("Please provide phone number")
                }

                addressTextField.text.isBlank() -> {
                    showValidationMessage("Please provide address")
                }

                nextOfKinNameTextField.text.isBlank() -> {
                    showValidationMessage("Please provide next of kin name")
                }

                nextOfKinPhoneNumberTextField.text.isNullOrBlank() -> {
                    showValidationMessage("Please provide next of kin phone number")
                }

                sessionComboBox.value.isNullOrBlank() -> {
                    showValidationMessage("Please select a session")
                }

                programmeComboBox.value.isNullOrBlank() -> {
                    showValidationMessage("Please select a programme")
                }

                else -> processSubmissionForm()
            }
        }


    private fun showValidationMessage(message: String) {
        showMessageDialog(
            rootPane,
            anchorPane,
            listOf(JFXButton("Okay")),
            "Warning!",
            message
        )
    }


    private fun clearFields() {
        val viewList = listOf(
            surnameTextField,
            firstNameTextField,
            otherNameTextField,
            registrationNumberTextField,
            phoneNumberTextField,
            emailTextField,
            addressTextField,
            passportTextField,
            nextOfKinAddressTextField,
            nextOfKinNameTextField,
            nextOfKinPhoneNumberTextField,
            disciplineTextField,
            dateOfBirthPicker.editor,
            supervisorTextField
        )

        for (view in viewList) {
            view.clear()
        }

        clearComboBox("Gender", genderComboBox)
        clearComboBox("Session", sessionComboBox)
        clearComboBox("Programme", programmeComboBox)
    }

    private fun clearComboBox(prompt: String, comboBox: ComboBox<String>) {
        comboBox.apply {
            selectionModel.clearSelection()
            promptText = prompt
            isEditable = true
        }

    }

}
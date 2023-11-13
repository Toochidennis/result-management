package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor.executeInsertQuery
import com.toochi.resultmanagement.backend.QueryExecutor.executeSelectAllQuery
import com.toochi.resultmanagement.models.Student
import com.toochi.resultmanagement.utils.Util.generateGender
import com.toochi.resultmanagement.utils.Util.generateProgrammes
import com.toochi.resultmanagement.utils.Util.generateSessions
import com.toochi.resultmanagement.utils.Util.showErrorDialog
import com.toochi.resultmanagement.utils.Util.showSuccessDialog
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

class StudentController {


    @FXML
    private lateinit var tabPane: TabPane

    // First tab
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


    //Second tab
    @FXML
    private lateinit var studentIdColumn: TableColumn<Student, Number>

    @FXML
    private lateinit var firstNameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var otherNameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var surnameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var sessionColumn: TableColumn<Student, String>

    @FXML
    private lateinit var programmeColumn: TableColumn<Student, String>

    @FXML
    private lateinit var disciplineColumn: TableColumn<Student, String>

    @FXML
    private lateinit var registrationNumberColumn: TableColumn<Student, String>

    @FXML
    private lateinit var phoneNumberColumn: TableColumn<Student, String>

    @FXML
    private lateinit var tableView: TableView<Student>

    private var selectedFile: File? = null


    @FXML
    fun initialize() {
        createProgrammes()
        createSessions()
        createGender()

//        tabPane.tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE

    }

    @FXML
    fun submitBtn() {
        processSubmissionForm()
    }

    @FXML
    fun uploadImageBtn() {
        selectedFile = chooseImageFile()

        if (selectedFile != null) {
            passportTextField.text = selectedFile?.name
        }
    }

    @FXML
    fun viewStudentTab() {
        createTableItems()
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
            showSuccessDialog("Student registered successfully!")
            clearFields()
        } else {
            showErrorDialog("Oops! Something went wrong, please check your connection and try again")
        }
    }

    private fun chooseImageFile(): File? {
        return FileChooser().apply {
            title = "Choose Image File"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter(
                    "Image Files",
                    "*.png", "*.jpg", "*.jpeg", "*.gif"
                )
            )
        }.showOpenDialog(Stage())
    }

    private fun getStudents(): ObservableList<Student> {
        val studentsResultSet = executeSelectAllQuery("students")
        val studentList = FXCollections.observableArrayList<Student>()

        while (studentsResultSet?.next() == true) {
            with(studentsResultSet) {
                studentList.add(
                    Student(
                        getInt("student_id"),
                        getString("surname"),
                        getString("first_name"),
                        getString("other_name"),
                        getString("registration_number"),
                        getString("phone_number"),
                        getString("discipline"),
                        getString("session"),
                        getString("programme"),
                        getString("date_of_birth"),
                        getString("email"),
                        getString("address")
                    )
                )
            }
        }

        return studentList
    }

    private fun createTableItems() {
        studentIdColumn.setCellValueFactory { SimpleIntegerProperty(it.value.studentId) }
        surnameColumn.setCellValueFactory { SimpleStringProperty(it.value.surname) }
        firstNameColumn.setCellValueFactory { SimpleStringProperty(it.value.firstName) }
        otherNameColumn.setCellValueFactory { SimpleStringProperty(it.value.otherName) }
        programmeColumn.setCellValueFactory { SimpleStringProperty(it.value.programme) }
        disciplineColumn.setCellValueFactory { SimpleStringProperty(it.value.discipline) }
        sessionColumn.setCellValueFactory { SimpleStringProperty(it.value.session) }
        registrationNumberColumn.setCellValueFactory { SimpleStringProperty(it.value.registrationNumber) }
        phoneNumberColumn.setCellValueFactory { SimpleStringProperty(it.value.phoneNumber) }

        tableView.items = getStudents()
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

    }
}
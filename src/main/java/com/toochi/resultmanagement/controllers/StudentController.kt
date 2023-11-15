package com.toochi.resultmanagement.controllers

import com.jfoenix.controls.JFXButton
import com.toochi.resultmanagement.backend.QueryExecutor.executeInsertQuery
import com.toochi.resultmanagement.backend.QueryExecutor.executeSelectAllQuery
import com.toochi.resultmanagement.models.Student
import com.toochi.resultmanagement.utils.Util.generateGender
import com.toochi.resultmanagement.utils.Util.generateProgrammes
import com.toochi.resultmanagement.utils.Util.generateSessions
import com.toochi.resultmanagement.utils.Util.showMessageDialog
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.io.File

class StudentController {

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var rootPane: StackPane

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

    @FXML
    private lateinit var searchTextField: TextField

    private var selectedFile: File? = null
    private val studentList = FXCollections.observableArrayList<Student>()


    @FXML
    fun initialize() {
        createProgrammes()
        createSessions()
        createGender()

    }

    @FXML
    fun submitBtn() {
        processSubmissionForm()
    }

    @FXML
    fun uploadImageBtn() {
        selectedFile = chooseImageFile()

        if (selectedFile != null) {
            val maxSize = 1024L
            val fileSize = selectedFile?.length()?.div(1024)
            val button = JFXButton("Okay")
            val header = "Error!"
            val body = "File must not be greater than 1MB!"

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

    @FXML
    fun viewStudentTab() {
        createTableItems()
    }

    private fun createProgrammes() {
        generateProgrammes(programmeComboBox)
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

        filterStudents()
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


    private fun filterStudents() {
        val filteredList = FilteredList(studentList)
        val sortedList = SortedList(filteredList)

        sortedList.comparatorProperty().bind(tableView.comparatorProperty())
        tableView.items = sortedList

        searchTextField.textProperty().addListener { _, _, newValue ->
            filteredList.setPredicate {
                if (newValue == null || newValue.isEmpty()) {
                    return@setPredicate true
                }

                // Filter based on your criteria, for example, name
                it.registrationNumber.contains(newValue, ignoreCase = true) or
                        it.surname.contains(newValue, ignoreCase = true)

            }
        }
    }
}
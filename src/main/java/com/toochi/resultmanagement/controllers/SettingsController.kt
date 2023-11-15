package com.toochi.resultmanagement.controllers

import com.jfoenix.controls.JFXButton
import com.toochi.resultmanagement.backend.QueryExecutor
import com.toochi.resultmanagement.backend.QueryExecutor.executeInsertQuery
import com.toochi.resultmanagement.backend.QueryExecutor.executeSelectAllQuery
import com.toochi.resultmanagement.backend.QueryExecutor.executeUpdateQuery
import com.toochi.resultmanagement.models.Settings
import com.toochi.resultmanagement.utils.Util.generateProgrammes
import com.toochi.resultmanagement.utils.Util.generateSemesters
import com.toochi.resultmanagement.utils.Util.refreshService
import com.toochi.resultmanagement.utils.Util.showMessageDialog
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import java.io.File

class SettingsController {


    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var rootPane: StackPane

    @FXML
    private lateinit var programmeComboBox: ComboBox<String>

    @FXML
    private lateinit var semesterComboBox: ComboBox<String>

    @FXML
    private lateinit var courseNameTextField: TextField

    @FXML
    private lateinit var courseCodeTexField: TextField

    @FXML
    private lateinit var courseUnitsTextField: TextField

    @FXML
    private lateinit var courseIdColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var courseNameColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var courseCodeColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var courseUnitsColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var programmeColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var semesterColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var tableView: TableView<Settings>

    private var courseId = 0


    @FXML
    fun addCourseBtn() {
        validateInput()
    }

    @FXML
    fun initialize() {
        generateProgrammes(programmeComboBox)
        createSemesters()

        existingRecord()
        onEditRecord()

        autoRefresh()
    }


    private fun createSemesters() {
        semesterComboBox.items = generateSemesters()
    }


    private fun createCourseTable(observableList: ObservableList<Settings>) {
        courseIdColumn.setCellValueFactory { SimpleIntegerProperty(it.value.courseId) }
        courseNameColumn.setCellValueFactory { SimpleStringProperty(it.value.courseName) }
        courseCodeColumn.setCellValueFactory { SimpleStringProperty(it.value.courseCode) }
        courseUnitsColumn.setCellValueFactory { SimpleIntegerProperty(it.value.courseUnits) }
        programmeColumn.setCellValueFactory { SimpleStringProperty(it.value.programme) }
        semesterColumn.setCellValueFactory { SimpleStringProperty(it.value.semester) }

        tableView.items = observableList
    }

    private fun processSubmissionForm() {
        val courseName = courseNameTextField.text
        val courseCode = courseCodeTexField.text
        val courseUnits = courseUnitsTextField.text
        val semester = semesterComboBox.value
        val programme = programmeComboBox.value

        val settings = Settings(
            0, 0,
            courseName, courseCode,
            courseUnits.toInt(),
            programme, semester
        )

        if (courseId != 0) {
            updateRecord(settings)
        } else {
            tableView.items.add(settings)
            insertRecord(settings)
        }
    }

    private fun insertRecord(settings: Settings) {
        val insertValues = hashMapOf<String, Any>(
            "course_name" to settings.courseName,
            "course_code" to settings.courseCode,
            "course_units" to settings.courseUnits,
            "programme" to settings.programme,
            "semester" to settings.semester
        )

        val affectedRows = executeInsertQuery("settings", insertValues)

        if (affectedRows == 1) {
            showMessage("success")
        } else {
            showMessage("error")
        }
    }

    private fun updateRecord(settings: Settings) {
        val updateValues = hashMapOf<String, Any>(
            "course_name" to settings.courseName,
            "course_code" to settings.courseCode,
            "course_units" to settings.courseUnits,
            "programme" to settings.programme,
            "semester" to settings.semester
        )

        val condition = hashMapOf<String, Any>(
            "course_id" to courseId,
        )

        val affectedRows = executeUpdateQuery("settings", updateValues, condition)

        if (affectedRows == 1) {
            showMessage("success")
        } else {
            showMessage("error")
        }

    }

    private fun existingRecord() {
        val settingsResultSet = executeSelectAllQuery("settings")
        val settingsList = FXCollections.observableArrayList<Settings>()

        while (settingsResultSet?.next() == true) {
            with(settingsResultSet) {
                settingsList.add(
                    Settings(
                        0, getInt("course_id"),
                        getString("course_name"),
                        getString("course_code"),
                        getInt("course_units"),
                        getString("programme"),
                        getString("semester")
                    )
                )
            }
        }

        createCourseTable(settingsList)
    }

    private fun onEditRecord() {
        with(tableView) {
            setOnMouseClicked {
                selectionModel.selectionMode = SelectionMode.SINGLE
                val selectedCourse = selectionModel.selectedItem

                if (selectedCourse != null) {
                    courseId = selectedCourse.courseId
                    courseNameTextField.text = selectedCourse.courseName
                    courseCodeTexField.text = selectedCourse.courseCode
                    courseUnitsTextField.text = (selectedCourse.courseUnits).toString()
                    programmeComboBox.value = selectedCourse.programme
                    semesterComboBox.value = selectedCourse.semester
                }
            }
        }
    }

    private fun validateInput() {
        when {
            courseNameTextField.text.isBlank() -> {
                showValidationMessage("Please provide a course name")
            }

            courseCodeTexField.text.isBlank() -> {
                showValidationMessage("Please provide a course code")
            }

            courseUnitsTextField.text.isBlank() -> {
                showValidationMessage("Please provide a course unit")
            }

            programmeComboBox.value.isNullOrBlank() -> {
                showValidationMessage("Please select a programme")
            }

            semesterComboBox.value.isNullOrBlank() -> {
                showValidationMessage("Please select a semester")
            }

            else -> {
                processSubmissionForm()
            }
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

    private fun showMessage(messageType: String) {
        when (messageType) {
            "success" -> {
                showMessageDialog(
                    rootPane,
                    anchorPane,
                    listOf(JFXButton("Okay")),
                    "Success",
                    "Settings saved successfully"
                )

                clearFields()
            }

            else -> {
                showMessageDialog(
                    rootPane,
                    anchorPane,
                    listOf(JFXButton("Okay, I'll check")),
                    "Error!",
                    "Oops! Something went wrong, please try again"
                )
            }
        }
    }

    private fun clearFields() {
        courseCodeTexField.clear()
        courseNameTextField.clear()
        courseUnitsTextField.clear()
    }

    private fun autoRefresh() {
        refreshService { existingRecord() }
    }
}
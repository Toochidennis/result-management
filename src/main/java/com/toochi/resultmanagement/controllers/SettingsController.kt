package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor.executeInsertQuery
import com.toochi.resultmanagement.backend.QueryExecutor.executeSelectAllQuery
import com.toochi.resultmanagement.models.Settings
import com.toochi.resultmanagement.utils.Util.generateProgrammes
import com.toochi.resultmanagement.utils.Util.generateSemesters
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField

class SettingsController {

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


    @FXML
    fun addCourseBtn() {
        prepareCourse()
    }

    @FXML
    fun initialize() {
        createProgrammes()
        createSemesters()

        existingSettings()
    }

    private fun createProgrammes() {
        programmeComboBox.items = generateProgrammes()
    }

    private fun createSemesters() {
        semesterComboBox.items = generateSemesters()
    }


    private fun createCourseTable(settings: Settings) {
        courseNameColumn.setCellValueFactory { SimpleStringProperty(it.value.courseName) }
        courseCodeColumn.setCellValueFactory { SimpleStringProperty(it.value.courseCode) }
        courseUnitsColumn.setCellValueFactory { SimpleIntegerProperty(it.value.courseUnits) }
        programmeColumn.setCellValueFactory { SimpleStringProperty(it.value.programme) }
        semesterColumn.setCellValueFactory { SimpleStringProperty(it.value.semester) }

        tableView.items.add(settings)
    }

    private fun prepareCourse() {
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

        createCourseTable(settings)

        insertCourse(settings)
    }

    private fun insertCourse(settings: Settings) {
        val values = hashMapOf<String, Any>(
            "course_name" to settings.courseName,
            "course_code" to settings.courseCode,
            "course_units" to settings.courseUnits,
            "programme" to settings.programme,
            "semester" to settings.semester
        )

        executeInsertQuery("settings", values)
    }

    private fun existingSettings() {
        val settingsResultSet = executeSelectAllQuery("settings")

        while (settingsResultSet?.next() == true) {
            with(settingsResultSet) {
                val settings = Settings(
                    0, getInt("course_id"),
                    getString("course_name"),
                    getString("course_code"),
                    getInt("course_units"),
                    getString("programme"),
                    getString("semester")
                )

                createCourseTable(settings)
            }
        }
    }

}
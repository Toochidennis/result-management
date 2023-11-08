package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor.executeInsertQuery
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
    }

    private fun createProgrammes() {
        programmeComboBox.items = generateProgrammes()
    }

    private fun createSemesters() {
        semesterComboBox.items = generateSemesters()
    }


    private fun createCourseTable() {
        courseNameColumn.setCellValueFactory { SimpleStringProperty(it.value.courseName) }
        courseCodeColumn.setCellValueFactory { SimpleStringProperty(it.value.courseCode) }
        courseUnitsColumn.setCellValueFactory { SimpleIntegerProperty(it.value.courseUnits) }
        programmeColumn.setCellValueFactory { SimpleStringProperty(it.value.programme) }
        semesterColumn.setCellValueFactory { SimpleStringProperty(it.value.semester) }
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
            courseUnits.toInt(), programme,
            semester
        )

        tableView.items.add(settings)

        createCourseTable()

        insertCourse(settings)
    }

    private fun insertCourse(settings: Settings) {
        val values = hashMapOf<String, Any>().apply {
            put("course_name", settings.courseName)
            put("course_code", settings.courseCode)
            put("course_units", settings.courseUnits)
            put("programme", settings.programme)
            put("semester", settings.semester)
        }

        val rows = executeInsertQuery("settings", values)

        println("Affected rows $rows")
    }

    private fun getExistingSettings(){

    }

}

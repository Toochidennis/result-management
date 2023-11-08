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

    private val settingsList = mutableListOf<Settings>()

    @FXML
    fun addCourseBtn() {
        prepareCourse()
    }

    @FXML
    fun programmeBtn() {

    }

    @FXML
    fun semesterBtn() {

    }

    @FXML
    fun initialize() {
        createProgrammes()
        createSemesters()
    }

    private fun createProgrammes(){
        programmeComboBox.items = generateProgrammes()
    }

    private fun createSemesters(){
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

        settingsList.add(
            Settings(
                0,
                0,
                courseName,
                courseCode,
                courseUnits.toInt(),
                programme,
                semester
            )
        )

        println("$courseName $courseCode $courseUnits $programme $semester")
        tableView.items.clear()

        settingsList.forEach { settings ->
            tableView.items.add(settings)
        }

        createCourseTable()

        insertCourse()
    }

    private fun insertCourse(){
        val values = hashMapOf<String, Any>()

        settingsList.forEach { settings ->
            values.apply {
                put("course_name", settings.courseName)
                put("course_code", settings.courseCode)
                put("course_units", settings.courseUnits)
                put("programme", settings.programme)
                put("semester", settings.semester)
            }
        }

       val rows =  executeInsertQuery("settings", values)

       println("Affected rows $rows")
    }

}

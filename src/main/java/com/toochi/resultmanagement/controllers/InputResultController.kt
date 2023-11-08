package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor.executeSelectWithConditionsQuery2
import com.toochi.resultmanagement.models.Settings
import com.toochi.resultmanagement.utils.Util.generateSemesters
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class InputResultController {


    @FXML
    private lateinit var searchTextField: TextField

    @FXML
    private lateinit var semesterComboBox: ComboBox<String>

    @FXML
    private lateinit var studentNameLabel: Label

    @FXML
    private lateinit var studentProgrammeLabel: Label

    @FXML
    private lateinit var vBox: VBox

    @FXML
    fun searchBtn() {
        getStudent()
    }

    private val settingsList = mutableListOf<Settings>()


    @FXML
    fun submitBtn() {
        processFormSubmission()
    }

    @FXML
    fun semesterBtn() {
        settingsList.clear()
        vBox.children.clear()
        getCourses()
    }

    @FXML
    fun initialize() {
        createSemesters()
    }

    private fun createSemesters() {
        semesterComboBox.items = generateSemesters()
    }

    private var programme: String? = null
    private fun getStudent() {
        val matricNumber = searchTextField.text
        val condition = hashMapOf<String, Any>().apply {
            put("matric_number", matricNumber)
        }

        val resultSet = executeSelectWithConditionsQuery2("students", condition)
        resultSet?.next()
        resultSet?.use {
            val studentId = it.getString("student_id")
            val studentName = "${it.getString("last_name")} ${it.getString("first_name")}"
            programme = it.getString("programme")

            studentNameLabel.text = studentName
            studentProgrammeLabel.text = programme
        }

    }

    private fun getCourses() {
        val semester = semesterComboBox.value

        if (semester.isNotBlank() && programme?.isNotBlank() == true) {
            val condition = hashMapOf<String, Any>().apply {
                put("semester", semester)
                put("programme", programme ?: "")
            }
            val resultSet = executeSelectWithConditionsQuery2("settings", condition)

            while (resultSet?.next() == true) {
                with(resultSet) {
                    settingsList.add(
                        Settings(
                            0, getInt("course_id"),
                            getString("course_name"), getString("course_code"),
                            getInt("course_units")
                        )
                    )
                }
            }

            settingsList.forEach { courseData ->
                val row = createCourseRow(courseData)
                vBox.children.addAll(row, Separator(Orientation.HORIZONTAL))
            }

        }
    }


    private fun createCourseRow(settings: Settings): HBox {
        val hBox = HBox().apply {
            spacing = 50.0
            prefHeight = 80.0
            padding = Insets(20.0, 0.0, 0.0, 0.0)
        }

        val serialNumberLabel = Label(settings.serialNumber.toString()).apply {
            prefHeight = 100.0
            prefWidth = 40.0
            padding = Insets(0.0, 0.0, 0.0, 50.0)
        }

        val courseNameLabel = Label(settings.courseName).apply {
            prefHeight = 40.0
            prefWidth = 240.0
            wrapTextProperty()
        }

        val courseCodeLabel = Label(settings.courseCode).apply {
            prefHeight = 40.0
            prefWidth = 100.0
        }

        val courseUnitsLabel = Label(settings.courseUnits.toString()).apply {
            prefHeight = 40.0
            prefWidth = 100.0
        }

        val gradeLaterTextField = TextField().apply {
            prefHeight = 40.0
            prefWidth = 100.0
        }
        gradeLaterTextField.textProperty().bindBidirectional(settings.gradeLaterProperty)

        val gradePointLabel = Label().apply {
            prefHeight = 40.0
            prefWidth = 100.0
        }
        gradePointLabel.textProperty().bind(settings.gradePointProperty.asString())

        val totalLabel = Label().apply {
            prefHeight = 40.0
            prefWidth = 100.0
            padding = Insets(0.0, 50.0, 0.0, 0.0)
        }
        totalLabel.textProperty().bind(settings.totalProperty.asString())

        hBox.children.addAll(
            serialNumberLabel,
            courseNameLabel,
            courseCodeLabel,
            courseUnitsLabel,
            gradeLaterTextField,
            gradePointLabel,
            totalLabel
        )

        return hBox
    }

    private fun processFormSubmission() {
        settingsList.forEach { courseData ->
            val serialNumber = courseData.serialNumber
            val courseName = courseData.courseName
            val courseCode = courseData.courseCode
            val gradeLater = courseData.gradeLaterProperty.get()
            val gradePoint = courseData.gradePointProperty.get()
            val total = courseData.totalProperty.get()

            println("Serial: $serialNumber, Name: $courseName, Code: $courseCode, Grade Later: $gradeLater, Grade Point: $gradePoint, Total: $total")
        }

    }

}
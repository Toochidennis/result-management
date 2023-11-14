package com.toochi.resultmanagement.controllers

import com.jfoenix.controls.JFXButton
import com.toochi.resultmanagement.backend.QueryExecutor.executeInsertQuery
import com.toochi.resultmanagement.backend.QueryExecutor.executeSelectWithConditionsQuery2
import com.toochi.resultmanagement.backend.QueryExecutor.executeUpdateQuery
import com.toochi.resultmanagement.backend.QueryExecutor.searchStudentByLastDigits
import com.toochi.resultmanagement.models.Settings
import com.toochi.resultmanagement.utils.Util.generateSemesters
import com.toochi.resultmanagement.utils.Util.showMessageDialog
import javafx.beans.property.StringProperty
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox

class InputResultController {


    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var rootPane: StackPane

    @FXML
    private lateinit var searchTextField: TextField

    @FXML
    private lateinit var semesterComboBox: ComboBox<String>

    @FXML
    private lateinit var studentProgrammeTextField: TextField

    @FXML
    private lateinit var studentNameTextField: TextField

    @FXML
    private lateinit var vBox: VBox

    private val settingsList = mutableListOf<Settings>()

    @FXML
    fun searchBtn() {
        searchStudent()
    }

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

        searchTextField.setOnKeyPressed {
            if (it.code == KeyCode.ENTER) {
                searchStudent()
            }
        }

        createSemesters()
    }

    private fun createSemesters() {
        semesterComboBox.items = generateSemesters()
    }

    private var programme: String? = null
    private var studentId: String? = null

    private fun searchStudent() {
        val matricNumber = searchTextField.text
        val searchResult = searchStudentByLastDigits(matricNumber)

        with(searchResult) {
            if (this?.next() == true) {
                clearFields()

                studentId = getString("student_id")
                val studentName = getString("surname") + " " +
                        getString("other_name") + " " +
                        getString("first_name")
                programme = getString("programme")

                studentNameTextField.text = studentName
                studentProgrammeTextField.text = programme
            } else {
                showMessageDialog(
                    rootPane,
                    anchorPane,
                    listOf(JFXButton("Okay, I'll check")),
                    "Error",
                    "Student with id ${searchTextField.text} does not exist"
                )
            }
        }
    }


    private fun clearFields() {
        vBox.children.clear()
        studentNameTextField.clear()
        studentProgrammeTextField.clear()
        semesterComboBox.selectionModel.clearSelection()
        semesterComboBox.promptTextProperty().value = "Semester"
    }

    private fun getCourses() {
        val semester = semesterComboBox.value

        if (semester.isNotBlank() && programme?.isNotBlank() == true) {
            val condition = hashMapOf<String, Any>().apply {
                put("semester", semester)
                put("programme", programme ?: "")
            }
            val resultSet = executeSelectWithConditionsQuery2("settings", condition)
            var count = 1

            while (resultSet?.next() == true) {
                with(resultSet) {
                    val existingRecordCondition = hashMapOf<String, Any>().apply {
                        put("course_id", getInt("course_id"))
                        put("student_id", studentId ?: 0)
                    }

                    val existingRecordResultSet =
                        executeSelectWithConditionsQuery2("results", existingRecordCondition)
                    var gradeLater = ""
                    var gradePoint = 0.0
                    var total = 0.0

                    while (existingRecordResultSet?.next() == true) {
                        with(existingRecordResultSet) {
                            gradeLater = getString("grade_later")
                            gradePoint = getDouble("grade_point")
                            total = getDouble("total")
                        }
                    }

                    settingsList.add(
                        Settings(
                            count, getInt("course_id"),
                            getString("course_name"),
                            getString("course_code"),
                            getInt("course_units"),
                            "", "",
                            gradeLater, gradePoint, total
                        )
                    )
                }

                count++
            }

            settingsList.forEach { courseData ->
                val row = createCourseRow(courseData)
                vBox.children.addAll(row, Separator(Orientation.HORIZONTAL))
            }

        }
    }


    private fun createCourseRow(settings: Settings): HBox {
        val hBox = HBox().apply {
            spacing = 20.0
            prefHeight = 80.0
            padding = Insets(20.0, 0.0, 0.0, 0.0)
        }

        val serialNumberLabel = createLabel(
            settings.serialNumber.toString(),
            100.0, 40.0, 0.0, 20.0
        )
        val courseNameLabel = createLabel(settings.courseName, 240.0, 40.0)
        val courseCodeLabel = createLabel(settings.courseCode, 100.0, 40.0)
        val courseUnitsLabel = createLabel(settings.courseUnits.toString(), 100.0, 40.0)
        val gradeLaterTextField = createTextField(settings.gradeLaterProperty)
        val gradePointLabel = createLabel("", 100.0, 40.0)
        val totalLabel = createLabel("", 100.0, 40.0, 20.0)

        gradePointLabel.textProperty().bind(settings.gradePointProperty.asString())
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

    private fun createLabel(
        text: String,
        width: Double,
        height: Double,
        paddingRight: Double = 0.0,
        paddingLeft: Double = 0.0
    ): Label {
        return Label(text).apply {
            prefWidth = width
            prefHeight = height
            padding = Insets(0.0, paddingRight, 0.0, paddingLeft)
        }
    }

    private fun createTextField(binding: StringProperty): TextField {
        val textField = TextField().apply {
            prefWidth = 100.0
            prefHeight = 40.0
            promptTextProperty().value = "Grade"
        }
        textField.textProperty().bindBidirectional(binding)
        return textField
    }

    private fun processFormSubmission() {
        settingsList.forEach { courseData ->
            val courseId = courseData.courseId
            val gradeLater = courseData.gradeLaterProperty.get()
            val gradePoint = courseData.gradePointProperty.get()
            val total = courseData.totalProperty.get()

            val studentIdInt = studentId?.toInt() ?: 0

            val existingRecordCondition = hashMapOf<String, Any>(
                "course_id" to courseId,
                "student_id" to studentIdInt
            )

            if (recordExists(existingRecordCondition)) {
                updateRecord(existingRecordCondition, gradeLater, gradePoint, total)
            } else {
                insertRecord(studentIdInt, courseId, gradeLater, gradePoint, total)
            }
        }
    }

    private fun recordExists(condition: HashMap<String, Any>): Boolean {
        val resultSet = executeSelectWithConditionsQuery2("results", condition)
        return resultSet?.next() == true
    }

    private fun updateRecord(
        condition: HashMap<String, Any>,
        gradeLater: String,
        gradePoint: Double,
        total: Double
    ) {
        val updateValues = hashMapOf<String, Any>(
            "grade_later" to gradeLater,
            "grade_point" to gradePoint,
            "total" to total
        )
        val affectedRows = executeUpdateQuery("results", updateValues, condition)

        if (affectedRows == 1) {
            showMessage("success")
        } else {
            showMessage("error")
        }
    }

    private fun insertRecord(
        studentId: Int,
        courseId: Int,
        gradeLater: String,
        gradePoint: Double,
        total: Double
    ) {
        val values = hashMapOf<String, Any>(
            "student_id" to studentId,
            "course_id" to courseId,
            "grade_later" to gradeLater,
            "grade_point" to gradePoint,
            "total" to total
        )
        val affectedRows = executeInsertQuery("results", values)

        if (affectedRows == 1) {
            showMessage("success")
        } else {
            showMessage("error")
        }
    }

    private fun showMessage(messageType: String) {
        when (messageType) {
            "success" -> {
                showMessageDialog(
                    rootPane,
                    anchorPane,
                    listOf(JFXButton("Okay")),
                    "Success",
                    "Result saved successfully"
                )
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

}
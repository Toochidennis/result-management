package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.models.Course
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox

class InputResultController {

    @FXML
    private lateinit var courseCodeLabel: Label

    @FXML
    private lateinit var courseNameLabel: Label

    @FXML
    private lateinit var courseUnitsLabel: Label

    @FXML
    private lateinit var gradeLaterTextField: TextField

    @FXML
    private lateinit var gradePontLabel: Label

    @FXML
    private lateinit var gradeTotalLabel: Label

    @FXML
    private lateinit var hBox: HBox

    @FXML
    private lateinit var searchTextField: TextField

    @FXML
    private lateinit var semesterComboBox: ComboBox<Any>

    @FXML
    private lateinit var studentNameLabel: Label

    @FXML
    private lateinit var studentProgrammeLabel: Label

    @FXML
    private lateinit var vBox: VBox

    @FXML
    fun searchBtn(event: MouseEvent) {

    }

    @FXML
    fun semesterComboBox(event: MouseEvent) {

    }


    private val courseList = mutableListOf<Course>()


    @FXML
    fun submitBtn() {
        //processFormSubmission()
    }

    @FXML
    fun initialize() {
        for (i in 1..10) {
            courseList.add(Course(i, "Course $i", "CSC10$i", i))
        }

        courseList.forEach { courseData ->
            val row = createRow(courseData)
            vBox.children.addAll(row, Separator(Orientation.HORIZONTAL))
        }

    }


    private fun createRow(course: Course): HBox {
        val hBox = HBox().apply {
            spacing = 50.0
            padding = Insets(50.0, 0.0, 0.0, 0.0)
        }

        val serialNumberLabel = Label(course.serialNumber.toString()).apply {
            prefHeight = 100.0
            prefWidth = 40.0
            padding = Insets(0.0, 0.0, 0.0, 50.0)
        }

        val courseNameLabel = Label(course.courseName).apply {
            prefHeight = 40.0
            prefWidth = 240.0
            wrapTextProperty()
        }

        val courseCodeLabel = Label(course.courseCode).apply {
            prefHeight = 40.0
            prefWidth = 100.0
        }

        val courseUnitsLabel = Label(course.courseUnits.toString()).apply {
            prefHeight = 40.0
            prefWidth = 100.0
        }

        val gradeLaterTextField = TextField().apply {
            prefHeight = 40.0
            prefWidth = 100.0
        }
        gradeLaterTextField.textProperty().bindBidirectional(course.gradeLaterProperty)

        val gradePointLabel = Label().apply {
            prefHeight = 40.0
            prefWidth = 100.0
        }
        gradePointLabel.textProperty().bind(course.gradePointProperty.asString())

        val totalLabel = Label().apply {
            prefHeight = 40.0
            prefWidth = 100.0
            padding = Insets(0.0, 50.0, 0.0, 0.0)
        }
        totalLabel.textProperty().bind(course.totalProperty.asString())

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

}


/*


private fun processFormSubmission() {
    for (courseData in courseList) {
        val serialNumber = courseData.serialNumber
        val courseName = courseData.courseName
        val courseCode = courseData.courseCode
        val gradeLater = courseData.gradeLaterProperty.get()
        val gradePoint = courseData.gradePointProperty.get()
        val total = courseData.totalProperty.get()

        // Now you can process or save these values to the database
        // Example: Insert into a database
        // insertIntoDatabase(serialNumber, courseName, courseCode, gradeLater, gradePoint, total)
        println("Serial: $serialNumber, Name: $courseName, Code: $courseCode, Grade Later: $gradeLater, Grade Point: $gradePoint, Total: $total")
    }
}*/

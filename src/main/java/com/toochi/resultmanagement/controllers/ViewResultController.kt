package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor.executeAllTablesQuery
import com.toochi.resultmanagement.models.Settings
import com.toochi.resultmanagement.utils.Util.openFileChooser
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXML
import javafx.print.PrinterJob
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.control.Label
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.AnchorPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.io.ByteArrayInputStream
import java.sql.ResultSet
import java.util.*
import java.util.prefs.Preferences

class ViewResultController {


    @FXML
    private lateinit var rootPane: AnchorPane

    @FXML
    private lateinit var nameLabel: Label

    @FXML
    private lateinit var passportImageView: ImageView

    @FXML
    private lateinit var programmeLabel: Label

    @FXML
    private lateinit var cgpaLabel: Label

    @FXML
    private lateinit var registrationNumberLabel: Label

    @FXML
    private lateinit var disciplineLabel: Label

    @FXML
    private lateinit var genderLabel: Label

    @FXML
    private lateinit var supervisorLabel: Label


    // First semester

    @FXML
    private lateinit var firstCourseCodeColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var firstCourseNameColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var firstCourseUnitsColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var firstCumulativeColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var firstCumulativeLabel: Label

    @FXML
    private lateinit var firstGradeLaterColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var firstGradePointColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var firstSemesterLabel: Label

    @FXML
    private lateinit var firstSerialNumberColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var firstTableView: TableView<Settings>

    @FXML
    private lateinit var firstUnitsLabel: Label


    // Second semester table
    @FXML
    private lateinit var secondCourseCodeColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var secondCourseNameColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var secondCourseUnitsColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var secondCumulativeColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var secondCumulativeLabel: Label

    @FXML
    private lateinit var secondGradeLaterColumn: TableColumn<Settings, String>

    @FXML
    private lateinit var secondGradePointColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var secondSemesterLabel: Label

    @FXML
    private lateinit var secondSerialNumberColumn: TableColumn<Settings, Number>

    @FXML
    private lateinit var secondTableView: TableView<Settings>

    @FXML
    private lateinit var secondUnitsLabel: Label

    @FXML
    private lateinit var sessionLabel: Label

    @FXML
    private lateinit var firstGPALabel: Label

    @FXML
    private lateinit var secondGPALabel: Label

    @FXML
    private lateinit var exitImageView: ImageView

    @FXML
    private lateinit var printImageView: ImageView

    private val firstSemesterResultList = FXCollections.observableArrayList<Settings>()
    private val secondSemesterResultList = FXCollections.observableArrayList<Settings>()
    private var isStudentDetails = true
    private var unitsSum1 = 0
    private var unitsSum2 = 0
    private var scoreSum1 = 0.0
    private var scoreSum2 = 0.0
    private var firstCount = 1
    private var secondCount = 1


    @FXML
    fun initialize() {
        val preferences = Preferences.userNodeForPackage(ViewResultController::class.java)
        val studentId = preferences.get("student_id", "")

        if (studentId == "-1") {
            rootPane.isVisible = false
        } else {
            val resultSet = executeAllTablesQuery(studentId.toInt())
            parseResultSet(resultSet)
        }
    }


    private fun parseResultSet(resultSet: ResultSet?) {
        with(resultSet) {
            while (this?.next() == true) {
                studentDetails(this)
                studentResult(this)
            }

            createFirstTable()
            createSecondTable()

            calculateCGPA()
        }
    }

    private fun studentDetails(resultSet: ResultSet) {
        if (isStudentDetails) {
            with(resultSet) {
                val studentName =
                    " ${getString("surname")} ${getString("other_name")} ${getString("first_name")}"
                val passport = getBytes("passport")

                nameLabel.text = studentName
                registrationNumberLabel.text = getString("registration_number")
                disciplineLabel.text = getString("discipline")
                supervisorLabel.text = getString("supervisor")
                genderLabel.text = getString("gender")
                sessionLabel.text = getString("session")
                programmeLabel.text = getString("programme")

                val imageStream = ByteArrayInputStream(passport)
                val image = Image(imageStream)
                passportImageView.image = image
            }

            isStudentDetails = !isStudentDetails
        }
    }

    private fun studentResult(resultSet: ResultSet) {
        with(resultSet) {
            val courseUnits = getInt("course_units")
            val total = getDouble("total")

            when (getString("semester")) {
                "First semester" -> {
                    firstSemesterResultList.add(
                        Settings(
                            firstCount++,
                            getInt("course_id"),
                            getString("course_name"),
                            getString("course_code"),
                            courseUnits,
                            "", "",
                            getString("grade_later"),
                            getDouble("grade_point"),
                            total
                        )
                    )

                    unitsSum1 += courseUnits
                    scoreSum1 += total
                }

                else -> {
                    secondSemesterResultList.add(
                        Settings(
                            secondCount++,
                            getInt("course_id"),
                            getString("course_name"),
                            getString("course_code"),
                            courseUnits,
                            "", "",
                            getString("grade_later"),
                            getDouble("grade_point"),
                            total
                        )
                    )

                    unitsSum2 += courseUnits
                    scoreSum2 += total
                }
            }
        }
    }


    private fun createFirstTable() {
        firstSerialNumberColumn.setCellValueFactory { SimpleIntegerProperty(it.value.serialNumber) }
        firstCourseNameColumn.setCellValueFactory { SimpleStringProperty(it.value.courseName) }
        firstCourseCodeColumn.setCellValueFactory { SimpleStringProperty(it.value.courseCode) }
        firstCourseUnitsColumn.setCellValueFactory { SimpleIntegerProperty(it.value.courseUnits) }
        firstGradeLaterColumn.setCellValueFactory { it.value.gradeLaterProperty }
        firstGradePointColumn.setCellValueFactory { it.value.gradePointProperty }
        firstCumulativeColumn.setCellValueFactory { it.value.totalProperty }

        val semester = "First semester ${sessionLabel.text}"
        firstSemesterLabel.text = semester
        firstUnitsLabel.text = unitsSum1.toString()
        firstCumulativeLabel.text = scoreSum1.toString()

        firstTableView.items = firstSemesterResultList
    }

    private fun createSecondTable() {
        secondSerialNumberColumn.setCellValueFactory { SimpleIntegerProperty(it.value.serialNumber) }
        secondCourseNameColumn.setCellValueFactory { SimpleStringProperty(it.value.courseName) }
        secondCourseCodeColumn.setCellValueFactory { SimpleStringProperty(it.value.courseCode) }
        secondCourseUnitsColumn.setCellValueFactory { SimpleIntegerProperty(it.value.courseUnits) }
        secondGradeLaterColumn.setCellValueFactory { it.value.gradeLaterProperty }
        secondGradePointColumn.setCellValueFactory { it.value.gradePointProperty }
        secondCumulativeColumn.setCellValueFactory { it.value.totalProperty }

        val semester = "Second semester ${sessionLabel.text}"
        secondSemesterLabel.text = semester
        secondUnitsLabel.text = unitsSum2.toString()
        secondCumulativeLabel.text = scoreSum2.toString()

        secondTableView.items = secondSemesterResultList
    }

    private fun calculateCGPA() {
        val totalUnits = unitsSum1 + unitsSum2
        val totalScores = scoreSum1 + scoreSum2
        val cgpa = totalScores / totalUnits.toDouble()
        val roundedCGPA = String.format(Locale.getDefault(), "%.2f", cgpa)
        cgpaLabel.text = roundedCGPA

        calculateGPA()
    }

    private fun calculateGPA() {
        val firstGPA = scoreSum1 / unitsSum1.toDouble()
        val roundedFirst = String.format(Locale.getDefault(), "%.2f", firstGPA)

        firstGPALabel.text = roundedFirst

        val secondGPA = scoreSum2 / unitsSum2.toDouble()
        val roundedSecond = String.format(Locale.getDefault(), "%.2f", secondGPA)

        secondGPALabel.text = roundedSecond
    }

    @FXML
    fun exitBtn() {
        val stage = exitImageView.scene.window as Stage
        stage.close()
    }

    @FXML
    fun printBtn() {
        val printerJob = PrinterJob.createPrinterJob()
        val window = printImageView.scene.window

        if (printerJob.showPageSetupDialog(window)) {
            if (printerJob.showPrintDialog(window)) {
                printerJob.printPage(rootPane)
                printerJob.endJob()
            }
        }
    }

    @FXML
    fun saveAsPDF() {
        val filters = FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf")
        val file =openFileChooser(rootPane.scene.window, filters, "Downloads", "Save")

        if (file != null) {
            val filePath = file.absolutePath
            generatePdf(rootPane, filePath)
            println("PDF saved successfully at: $filePath")
        }
    }

    private fun generatePdf(root: Node, filePath: String) {
        val document = PDDocument()
        val page = PDPage()
        document.addPage(page)

        val contentStream = PDPageContentStream(document, page)

        val scaleX = 0.75
        val scaleY = 0.75

        val snapshot = SnapshotParameters()
        //   snapshot.transform = Transform.scale(scaleX, scaleY)
        val writableImage =
            WritableImage(root.boundsInLocal.width.toInt(), root.boundsInLocal.height.toInt())
        root.snapshot(snapshot, writableImage)

        val bufferedImage = SwingFXUtils.fromFXImage(writableImage, null)
        val pdImageXObject = LosslessFactory.createFromImage(document, bufferedImage)

        val width = bufferedImage.width * scaleX
        val height = bufferedImage.height * scaleY

        contentStream.drawImage(
            pdImageXObject,
            0f,
            (page.cropBox.upperRightY - height).toFloat(),
            width.toFloat(),
            height.toFloat()
        )

        contentStream.close()

        document.save(filePath)
        document.close()
    }
}

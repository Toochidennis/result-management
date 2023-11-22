package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor.executeAllTablesQuery
import com.toochi.resultmanagement.models.Result
import com.toochi.resultmanagement.utils.Util.openFileChooser
import javafx.collections.FXCollections
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.print.PrinterJob
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
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
    private lateinit var resultPane: AnchorPane

    @FXML
    private lateinit var academicSessionLabel: Label

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
    private lateinit var resultVBox: VBox

    private val firstSemesterResultList = FXCollections.observableArrayList<Result>()
    private val secondSemesterResultList = FXCollections.observableArrayList<Result>()
    private var isStudentDetails = true
    private var sessionText = ""
    private var firstCount = 1
    private var secondCount = 1
    private var unitsSum1 = 0
    private var unitsSum2 = 0
    private var scoreSum1 = 0.0
    private var scoreSum2 = 0.0


    @FXML
    fun initialize() {
        val preferences = Preferences.userNodeForPackage(ViewResultController::class.java)
        val studentId = preferences.get("student_id", "")

        val resultSet = executeAllTablesQuery(studentId.toInt())
        parseResultSet(resultSet)
    }

    private fun createTitleRow(term: String): HBox {
        return HBox().apply {
            alignment = Pos.BASELINE_CENTER
            children.add(Label("$sessionText $term Semester").apply {
                style = "-fx-font-weight: bold;" +
                        "-fx-font-size: 16;"

            })
        }
    }

    private fun createHeadingRow(): HBox {
        val serialNumberLabel = createLabel("S/N", 30.0, true)
        val codeLabel = createLabel("Code", 80.0, true)
        val titleLabel = createLabel("Title", 420.0, true)
        val unitLabel = createLabel("Unit", 30.0, true)
        val gradeLabel = createLabel("Grade", 40.0, true)
        val gradePointLabel = createLabel("GP", 30.0, true)
        val wgpLabel = createLabel("WGP", 42.0, true)

        val labelList = mutableListOf(
            serialNumberLabel,
            codeLabel,
            titleLabel,
            unitLabel,
            gradeLabel,
            gradePointLabel,
            wgpLabel
        )

        return children(labelList)

    }

    private fun createItems(result: Result): HBox {
        val serialNumberLabel = createLabel(result.id, 30.0, true, Pos.CENTER)
        val codeLabel = createLabel(result.code, 80.0, false)
        val titleLabel = createLabel(result.title, 420.0, false)
        val unitLabel = createLabel(result.unit, 30.0, false, Pos.CENTER)
        val gradeLabel = createLabel(result.grade, 40.0, false, Pos.CENTER)
        val gradePointLabel = createLabel(result.gradePoint, 30.0, false, Pos.CENTER)
        val wgpLabel = createLabel(result.wgp, 42.0, false, Pos.CENTER)

        val labelList = mutableListOf(
            serialNumberLabel,
            codeLabel,
            titleLabel,
            unitLabel,
            gradeLabel,
            gradePointLabel,
            wgpLabel
        )

        return children(labelList)
    }

    private fun children(labelList: MutableList<Label>): HBox {
        val hBox = HBox()
        val lastLabel = labelList.last()

        labelList.forEach { label ->
            if (lastLabel == label) {
                hBox.children.addAll(
                    Separator(Orientation.VERTICAL),
                    label,
                    Separator(Orientation.VERTICAL)
                )

            } else {
                hBox.children.addAll(Separator(Orientation.VERTICAL), label)
            }
        }

        return hBox
    }

    private fun createTotalRow(result: Result): HBox {
        val serialNumberLabel = createLabel("", 30.0, true, Pos.CENTER)
        val codeLabel = createLabel("", 80.0, false)
        val titleLabel = createLabel(result.title, 430.0, true, Pos.BASELINE_RIGHT)
        val unitLabel = createLabel(result.unit, 30.0, true, Pos.CENTER)
        val gradeLabel = createLabel("", 40.0, false, Pos.CENTER)
        val gradePointLabel = createLabel("", 40.0, false, Pos.CENTER)
        val wgpLabel = createLabel(result.wgp, 42.0, true, Pos.CENTER)

        return HBox().apply {
            children.addAll(
                Separator(Orientation.VERTICAL),
                serialNumberLabel,
                codeLabel,
                titleLabel,
                Separator(Orientation.VERTICAL),
                unitLabel,
                gradeLabel,
                gradePointLabel,
                Separator(Orientation.VERTICAL),
                wgpLabel,
                Separator(Orientation.VERTICAL)
            )
        }
    }

    private fun createGPARow(result: Result): HBox {
        val serialNumberLabel = createLabel("", 30.0)
        val codeLabel = createLabel("", 80.0)
        val titleLabel = createLabel(result.title, 430.0, true, Pos.BASELINE_RIGHT)
        val unitLabel = createLabel(result.unit, 40.0, true, Pos.CENTER)
        val gradeLabel = createLabel("", 40.0)
        val gradePointLabel = createLabel("", 30.0)
        val wgpLabel = createLabel(result.wgp, 42.0)

        return HBox().apply {
            children.addAll(
                Separator(Orientation.VERTICAL),
                serialNumberLabel,
                codeLabel,
                titleLabel,
                Separator(Orientation.VERTICAL),
                unitLabel,
                gradeLabel,
                gradePointLabel,
                Separator(Orientation.VERTICAL),
                wgpLabel,
                Separator(Orientation.VERTICAL)
            )
        }
    }

    private fun createContainer(container: VBox, term: String) {
        container.children.addAll(
            Separator(Orientation.HORIZONTAL),
            createTitleRow(term),
            Separator(Orientation.HORIZONTAL),
            createHeadingRow(),
            Separator(Orientation.HORIZONTAL)
        )
    }

    private fun createLabel(
        text: String,
        width: Double = 0.0,
        isBold: Boolean = false,
        alignment: Pos = Pos.BASELINE_LEFT,
        paddingRight: Double = 0.0,
        paddingLeft: Double = 0.0
    ): Label {
        return Label(text).apply {
            prefWidth = width
            //prefHeight = 40.0
            padding = Insets(0.0, paddingRight, 0.0, paddingLeft)
            this.alignment = alignment

            style = if (isBold) {
                "-fx-font-weight: bold; " +
                        "-fx-font-size: 14;"
            } else {
                "-fx-font-size: 16;"
            }
        }
    }

    private fun parseResultSet(resultSet: ResultSet?) {
        with(resultSet) {
            while (this?.next() == true) {
                studentDetails(this)
                studentResult(this)
            }
        }

        resultVBox.apply {
            spacing = 20.0

            children.addAll(
                createFirst(),
                createSecond()
            )
        }

        calculateCGPA()
    }

    private fun studentDetails(resultSet: ResultSet) {
        if (isStudentDetails) {
            with(resultSet) {
                val studentName = "${getString("surname")} " +
                        "${getString("other_name")} ${getString("first_name")}"
                val passport = getBytes("passport")
                nameLabel.text = studentName
                registrationNumberLabel.text = getString("registration_number")
                disciplineLabel.text = getString("discipline")
                sessionText = getString("session")
                programmeLabel.text = getString("programme")
                val imageStream = ByteArrayInputStream(passport)
                val image = Image(imageStream)
                passportImageView.image = image

                academicSessionLabel.text = "$sessionText ACADEMIC SESSION RESULTS"
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
                        Result(
                            (firstCount++).toString(),
                            getString("course_code"),
                            getString("course_name"),
                            (courseUnits).toString(),
                            getString("grade_letter"),
                            (getInt("grade_point")).toString(),
                            (total).toString()
                        )
                    )

                    unitsSum1 += courseUnits
                    scoreSum1 += total
                }

                else -> {
                    secondSemesterResultList.add(
                        Result(
                            (secondCount++).toString(),
                            getString("course_code"),
                            getString("course_name"),
                            (courseUnits).toString(),
                            getString("grade_letter"),
                            (getInt("grade_point")).toString(),
                            (total).toString()
                        )
                    )

                    unitsSum2 += courseUnits
                    scoreSum2 += total
                }
            }
        }
    }

    private fun createFirst(): VBox {
        val vBox = VBox()
        createContainer(vBox, "First")
        firstSemesterResultList.forEach { result ->
            vBox.children.addAll(createItems(result), Separator(Orientation.HORIZONTAL))
        }

        val totalResult = Result(
            "", "", "Total",
            unitsSum1.toString(), "",
            "", scoreSum1.toString()
        )

        val gpaResult = Result(
            "", "", "GPA",
            calculateFirstGPA(), "",
            "", ""
        )

        vBox.children.addAll(
            createTotalRow(totalResult),
            Separator(Orientation.HORIZONTAL),
            createGPARow(gpaResult),
            Separator(Orientation.HORIZONTAL)
        )

        return vBox
    }

    private fun createSecond(): VBox {
        val vBox = VBox()

        createContainer(vBox, "Second")

        secondSemesterResultList.forEach { result ->
            vBox.children.addAll(createItems(result), Separator(Orientation.HORIZONTAL))
        }

        val totalResult = Result(
            "", "", "Total",
            unitsSum2.toString(), "",
            "", scoreSum2.toString()
        )

        val gpaResult = Result(
            "", "", "GPA",
            calculateSecondGPA(), "",
            "", ""
        )

        vBox.children.addAll(
            createTotalRow(totalResult),
            Separator(Orientation.HORIZONTAL),
            createGPARow(gpaResult),
            Separator(Orientation.HORIZONTAL)
        )

        return vBox
    }

    private fun calculateFirstGPA(): String {
        val gpa = scoreSum1 / unitsSum1
        return String.format(Locale.getDefault(), "%.2f", gpa)
    }

    private fun calculateSecondGPA(): String {
        val gpa = scoreSum2 / unitsSum2
        return String.format(Locale.getDefault(), "%.2f", gpa)
    }

    private fun calculateCGPA() {
        val totalUnits = unitsSum1 + unitsSum2
        val totalWGP = scoreSum1 + scoreSum2
        val cgpa = totalWGP / totalUnits
        val roundedCgpa = String.format(Locale.getDefault(), "%.2f", cgpa)

        cgpaLabel.text = roundedCgpa
    }


    @FXML
    fun exitBtn() {

        println("What")
        val stage = resultPane.scene.window as Stage
        stage.close()
    }

    @FXML
    fun printBtn() {
        val printerJob = PrinterJob.createPrinterJob()
        val window = resultPane.scene.window

        if (printerJob.showPageSetupDialog(window)) {
            if (printerJob.showPrintDialog(window)) {
                printerJob.printPage(resultPane)
                printerJob.endJob()
            }
        }
    }

    @FXML
    fun saveBtn() {
        val filters = FileChooser.ExtensionFilter("PDF Files (.pdf)", ".pdf")
        val file = openFileChooser(resultPane.scene.window, filters, "Downloads", "Save")

        if (file != null) {
            val filePath = file.absolutePath
            generatePdf(resultPane, filePath)
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
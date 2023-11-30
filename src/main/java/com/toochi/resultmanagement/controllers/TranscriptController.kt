package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor
import com.toochi.resultmanagement.models.Result
import com.toochi.resultmanagement.utils.Util
import com.toochi.resultmanagement.utils.Util.generatePdf
import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.print.PrinterJob
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import java.sql.ResultSet
import java.util.prefs.Preferences

class TranscriptController {

    @FXML
    private lateinit var nameLabel: Label

    @FXML
    private lateinit var regNoLabel: Label

    @FXML
    private lateinit var resultPane: AnchorPane

    @FXML
    private lateinit var resultVBox: VBox

    @FXML
    private lateinit var yearOfEntryLabel: Label

    private val firstSemesterResultList = FXCollections.observableArrayList<Result>()
    private val secondSemesterResultList = FXCollections.observableArrayList<Result>()
    private var isStudentDetails = true
    private var unitsSum1 = 0
    private var unitsSum2 = 0
    private var scoreSum1 = 0.0
    private var scoreSum2 = 0.0


    @FXML
    fun initialize() {
        val preferences = Preferences.userNodeForPackage(StatementOfResultController::class.java)
        val studentId = preferences.get("student_id", "")

        val resultSet = QueryExecutor.executeAllTablesQuery(studentId.toInt())
        parseResultSet(resultSet)
    }

    private fun createHeadingRow(): HBox {
        val departmentLabel = createLabel("Dept.", 40.0, true)
        val codeLabel = createLabel("Code", 90.0, true)
        val titleLabel = createLabel("Title", 425.0, true)
        val unitLabel = createLabel("Unit", 30.0, true)
        val gradeLabel = createLabel("Grade", 40.0, true)
        val gradePointLabel = createLabel("Point", 40.0, true)
        val remarkLabel = createLabel("Remark", 80.0, true)

        val labelList = mutableListOf(
            departmentLabel,
            codeLabel,
            titleLabel,
            unitLabel,
            gradeLabel,
            gradePointLabel,
            remarkLabel
        )

        return children(labelList)
    }

    private fun children(labelList: MutableList<Label>): HBox {
        val lastLabel = labelList.last()

        return HBox().apply {
            labelList.forEach { label ->
                if (lastLabel == label) {
                    children.addAll(
                        Separator(Orientation.VERTICAL),
                        label,
                        Separator(Orientation.VERTICAL)
                    )
                } else {
                    children.addAll(Separator(Orientation.VERTICAL), label)
                }
            }
        }
    }

    private fun createResultItems(result: Result): HBox {
        val departmentLabel = createLabel(result.id, 40.0, true, Pos.CENTER)
        val codeLabel = createLabel(result.code, 90.0, false)
        val titleLabel = createLabel(result.title, 425.0, false)
        val unitLabel = createLabel(result.unit, 30.0, false, Pos.CENTER)
        val gradeLabel = createLabel(result.grade, 40.0, false, Pos.CENTER)
        val gradePointLabel = createLabel(result.wgp, 40.0, false, Pos.CENTER)
        val wgpLabel = createLabel("", 80.0, false, Pos.CENTER)

        val labelList = mutableListOf(
            departmentLabel,
            codeLabel,
            titleLabel,
            unitLabel,
            gradeLabel,
            gradePointLabel,
            wgpLabel
        )

        return children(labelList)
    }

    private fun createTotalRow(result: Result): HBox {
        val departmentLabel = createLabel("", 50.0, true, Pos.CENTER)
        val codeLabel = createLabel("", 90.0, false)
        val titleLabel = createLabel(result.title, 430.0, true, Pos.BASELINE_RIGHT)
        val unitLabel = createLabel(result.unit, 40.0, true, Pos.CENTER)
        val gradeLabel = createLabel("", 40.0, false, Pos.CENTER)
        val gradePointLabel = createLabel(result.wgp, 50.0, true, Pos.CENTER)
        val remarkLabel = createLabel("", 42.0, true, Pos.CENTER)

        return HBox().apply {
            children.addAll(
                departmentLabel,
                codeLabel,
                titleLabel,
                unitLabel,
                gradeLabel,
                gradePointLabel,
                remarkLabel,
            )
        }
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

    private fun createResultContainer(container: VBox) {
        container.children.addAll(
            Separator(Orientation.HORIZONTAL),
            createHeadingRow(),
            Separator(Orientation.HORIZONTAL)
        )
    }

    private fun createFooterContainer(): VBox {
        val titleList = listOf(
            "External Examiner:",
            "Internal Examiner:",
            "Head of Department:",
            "Dean of Faculty:"
        )

        return VBox().apply {
            padding = Insets(50.0, 0.0, 0.0, 0.0)
            spacing = 30.0
            children.apply {
                add(createFooterHeader())
                listOf("(i)", "(ii)", "(iii)", "(iv)").forEachIndexed { index, numeral ->
                    addAll(
                        createFooterItems(numeral, titleList[index])
                    )
                }
            }
        }
    }

    private fun createFooterHeader(): HBox {
        return HBox().apply {
            children.addAll(
                createLabel("", 50.0),
                createLabel("", 180.0),
                createLabel("Name", 300.0, true, Pos.BASELINE_CENTER),
                createLabel("Signature", 150.0, true, Pos.BASELINE_CENTER),
                createLabel("Date", 150.0, true, Pos.BASELINE_CENTER)
            )
        }
    }

    private fun createFooterItems(id: String, title: String): HBox {
        return HBox().apply {
            spacing = 20.0

            children.addAll(
                createLabel(id, 50.0),
                createLabel(title, 180.0),
                createLabel(
                    "....................................................................................",
                    300.0
                ),
                createLabel("..............................................", 150.0),
                createLabel("..............................................", 150.0)
            )
        }
    }

    private fun createResultTable(): VBox {
        val listSum = firstSemesterResultList + secondSemesterResultList

        return VBox().apply {
            createResultContainer(this)

            listSum.forEach { result ->
                children.addAll(
                    createResultItems(result),
                    Separator(Orientation.HORIZONTAL)
                )
            }

            val totalResult = Result(
                "", "", "Total",
                unitsSum1.toString(), "",
                "", scoreSum1.toString()
            )

            children.addAll(createTotalRow(totalResult))
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
                createResultTable(),
                createFooterContainer()
            )
        }
    }

    private fun studentDetails(resultSet: ResultSet) {
        if (isStudentDetails) {
            with(resultSet) {
                val studentName = "${getString("surname")} " +
                        "${getString("other_name")} ${getString("first_name")}"
                nameLabel.text = studentName.uppercase()
                regNoLabel.text = getString("registration_number")
                yearOfEntryLabel.text = getString("session").removeRange(0, 5)
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
                            "HAM",
                            getString("course_code"),
                            getString("course_name"),
                            (courseUnits).toString(),
                            getString("grade_letter"),
                            "",
                            (total).toString()
                        )
                    )

                    unitsSum1 += courseUnits
                    scoreSum1 += total
                }

                else -> {
                    secondSemesterResultList.add(
                        Result(
                            "HAM",
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


    @FXML
    fun exitBtn() {
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
        val file = Util.openFileChooser(resultPane.scene.window, filters, "Downloads", "Save")

        if (file != null) {
            val filePath = file.absolutePath
            generatePdf(resultPane, filePath)
        }
    }

}

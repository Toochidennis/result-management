package com.toochi.resultmanagement.controllers

import com.jfoenix.controls.JFXButton
import com.toochi.resultmanagement.backend.QueryExecutor.executeStudentsWithResultQuery
import com.toochi.resultmanagement.models.Student
import com.toochi.resultmanagement.utils.Util.refreshService
import com.toochi.resultmanagement.utils.Util.showMessageDialog
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.StackPane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.util.prefs.Preferences

class ViewStudentResultController {


    @FXML
    private lateinit var rootPane: StackPane

    @FXML
    private lateinit var anchorPane: AnchorPane

    @FXML
    private lateinit var searchTextField: TextField

    @FXML
    private lateinit var disciplineColumn: TableColumn<Student, String>

    @FXML
    private lateinit var firstnameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var otherNameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var programmeColumn: TableColumn<Student, String>

    @FXML
    private lateinit var registrationNumberColumn: TableColumn<Student, String>

    @FXML
    private lateinit var studentIdColumn: TableColumn<Student, Number>

    @FXML
    private lateinit var sessionColumn: TableColumn<Student, String>

    @FXML
    private lateinit var surnameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var tableView: TableView<Student>

    private val studentList = FXCollections.observableArrayList<Student>()

    @FXML
    fun initialize() {
        createTableItems()
        databaseRefresh()
    }


    private fun getStudents(): ObservableList<Student> {
        val studentsResultSet = executeStudentsWithResultQuery("students")
        studentList.clear()

        while (studentsResultSet?.next() == true) {
            with(studentsResultSet) {
                studentList.add(
                    Student(
                        getInt("student_id"),
                        getString("surname"),
                        getString("first_name"),
                        getString("other_name"),
                        getString("registration_number"),
                        getString("phone_number"),
                        getString("discipline"),
                        getString("session"),
                        getString("programme"),
                        getString("date_of_birth"),
                        getString("email"),
                        getString("address")
                    )
                )
            }
        }

        return studentList
    }

    private fun createTableItems() {
        studentIdColumn.setCellValueFactory { SimpleIntegerProperty(it.value.studentId) }
        surnameColumn.setCellValueFactory { SimpleStringProperty(it.value.surname) }
        firstnameColumn.setCellValueFactory { SimpleStringProperty(it.value.firstName) }
        otherNameColumn.setCellValueFactory { SimpleStringProperty(it.value.otherName) }
        programmeColumn.setCellValueFactory { SimpleStringProperty(it.value.programme) }
        disciplineColumn.setCellValueFactory { SimpleStringProperty(it.value.discipline) }
        sessionColumn.setCellValueFactory { SimpleStringProperty(it.value.session) }
        registrationNumberColumn.setCellValueFactory { SimpleStringProperty(it.value.registrationNumber) }

        tableView.items = getStudents()

        filterStudents()
    }

    private fun databaseRefresh() {
        refreshService { createTableItems() }
    }

    @FXML
    fun resultDetails() {
        tableView.selectionModel.selectionMode = SelectionMode.SINGLE

        val selectedStudent = tableView.selectionModel.selectedItem
        val studentId = selectedStudent?.studentId

        if (studentId != null && studentId != -1) {
            Preferences.userNodeForPackage(ViewStudentResultController::class.java).apply {
                put("student_id", (studentId).toString())
            }
            tableView.selectionModel.clearSelection()

            val transcriptBtn = JFXButton("Transcript")
            val statementOfResultBtn = JFXButton("Statement of result")

            showMessageDialog(
                rootPane,
                anchorPane,
                listOf(
                    transcriptBtn to {
                        loadStage("transcript", "Transcript")
                    },

                    statementOfResultBtn to {
                        loadStage("statement_of_result", "Statement of result")
                    }
                ),
                "Select an option to view",
            )

        }

    }

    private fun loadStage(viewName: String, title: String) {
        try {
            val fxmlLoader = FXMLLoader(
                javaClass.getResource("/com/toochi/resultmanagement/result/$viewName.fxml")
            )

            Stage().apply {
                scene = Scene(fxmlLoader.load())
                initModality(Modality.APPLICATION_MODAL)
                initStyle(StageStyle.UNDECORATED)
                this.title = title

            }.showAndWait()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterStudents() {
        val filteredList = FilteredList(studentList)
        val sortedList = SortedList(filteredList)

        sortedList.comparatorProperty().bind(tableView.comparatorProperty())
        tableView.items = sortedList

        searchTextField.textProperty().addListener { _, _, newValue ->
            filteredList.setPredicate {
                if (newValue == null || newValue.isEmpty()) {
                    return@setPredicate true
                }

                // Filter based on your criteria, for example, name
                it.registrationNumber.contains(newValue, ignoreCase = true) or
                        it.surname.contains(newValue, ignoreCase = true)
            }
        }
    }

}
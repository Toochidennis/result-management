package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor.executeStudentsWithResultQuery
import com.toochi.resultmanagement.models.Student
import com.toochi.resultmanagement.utils.Util.refreshService
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
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.util.prefs.Preferences

class ResultListController {


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
    fun searchBtn() {

    }

    @FXML
    fun initialize() {
        databaseRefresh()
        viewStudentResult()
    }


    private fun getStudents(): ObservableList<Student> {
        val studentsResultSet = executeStudentsWithResultQuery("students")

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

    private fun viewStudentResult() {
        tableView.selectionModel.selectionMode = SelectionMode.SINGLE

        tableView.setOnMouseClicked {
            try {
                val selectedStudent = tableView.selectionModel.selectedItem
                val studentId = selectedStudent?.studentId ?: -1
                Preferences.userNodeForPackage(ResultListController::class.java).apply {
                    put("student_id", studentId.toString())
                }

                val fxmlLoader = FXMLLoader(
                    javaClass.getResource("/com/toochi/resultmanagement/result/view_result.fxml")
                )

                Stage().apply {
                    scene = Scene(fxmlLoader.load())
                    initModality(Modality.APPLICATION_MODAL)
                    initStyle(StageStyle.UNDECORATED)
                    title = "Student result"

                }.showAndWait()

            } catch (e: Exception) {
                e.printStackTrace()
            }
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

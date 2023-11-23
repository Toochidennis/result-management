package com.toochi.resultmanagement.controllers

import com.toochi.resultmanagement.backend.QueryExecutor
import com.toochi.resultmanagement.models.Student
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.collections.transformation.SortedList
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField

class ViewStudentController {


    @FXML
    private lateinit var studentIdColumn: TableColumn<Student, Number>

    @FXML
    private lateinit var firstNameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var otherNameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var surnameColumn: TableColumn<Student, String>

    @FXML
    private lateinit var sessionColumn: TableColumn<Student, String>

    @FXML
    private lateinit var programmeColumn: TableColumn<Student, String>

    @FXML
    private lateinit var disciplineColumn: TableColumn<Student, String>

    @FXML
    private lateinit var registrationNumberColumn: TableColumn<Student, String>

    @FXML
    private lateinit var phoneNumberColumn: TableColumn<Student, String>

    @FXML
    private lateinit var tableView: TableView<Student>

    @FXML
    private lateinit var searchTextField: TextField

    private val studentList = FXCollections.observableArrayList<Student>()



    @FXML
    fun initialize(){
        createTableItems()
    }

    private fun getStudents(): ObservableList<Student> {
        val studentsResultSet = QueryExecutor.executeSelectAllQuery("students")

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
        firstNameColumn.setCellValueFactory { SimpleStringProperty(it.value.firstName) }
        otherNameColumn.setCellValueFactory { SimpleStringProperty(it.value.otherName) }
        programmeColumn.setCellValueFactory { SimpleStringProperty(it.value.programme) }
        disciplineColumn.setCellValueFactory { SimpleStringProperty(it.value.discipline) }
        sessionColumn.setCellValueFactory { SimpleStringProperty(it.value.session) }
        registrationNumberColumn.setCellValueFactory { SimpleStringProperty(it.value.registrationNumber) }
        phoneNumberColumn.setCellValueFactory { SimpleStringProperty(it.value.phoneNumber) }

        tableView.items = getStudents()

        filterStudents()

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

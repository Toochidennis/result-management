package com.toochi.resultmanagement.controllers

import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.input.MouseEvent

class SettingsController {

    @FXML
    private lateinit var courseCodeColumn: TableColumn<Any, Any>

    @FXML
    private lateinit var courseCodeTexField: TextField

    @FXML
    private lateinit var courseNameColumn: TableColumn<Any, Any>

    @FXML
    private lateinit var courseNameTextField: TextField

    @FXML
    private lateinit var courseUnitsColumn: TableColumn<Any, Any>

    @FXML
    private lateinit var courseUnitsTextField: TextField

    @FXML
    private lateinit var programmeColumn: TableColumn<Any, Any>

    @FXML
    private lateinit var semesterColumn: TableColumn<Any, Any>

    @FXML
    private lateinit var tableView: TableView<Any>

    @FXML
    fun addCourseBtn(event: MouseEvent) {

    }

    @FXML
    fun programmeComboBox(event: MouseEvent) {

    }

    @FXML
    fun semesterComboBox(event: MouseEvent) {

    }

}

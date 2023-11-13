package com.toochi.resultmanagement.utils

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.fxml.FXMLLoader
import javafx.scene.control.Alert
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import javafx.util.Duration
import java.time.Year

object Util {

    @JvmStatic
    fun loadFXMLScene(root: BorderPane, fxmlFileName: String) {
        try {
            val loader =
                FXMLLoader(javaClass.getResource("/com/toochi/resultmanagement/$fxmlFileName"))
            val layout = loader.load<AnchorPane>()
            root.center = layout
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun generateSessions(): ObservableList<String> {
        val sessionList = FXCollections.observableArrayList<String>()

        for (i in 2005..2030) {
            sessionList.add("${i - 1}/$i")
        }

        return sessionList
    }

    @JvmStatic
    fun generateSemesters(): ObservableList<String> =
        FXCollections.observableArrayList(
            "First semester",
            "Second semester"
        )

    @JvmStatic
    fun generateProgrammes(): ObservableList<String> =
        FXCollections.observableArrayList("PGD", "MBA", "MSC")

    @JvmStatic
    fun generateGender(): ObservableList<String> =
        FXCollections.observableArrayList("Male", "Female", "Prefer not to say")


    @JvmStatic
    fun showSuccessDialog(message: String) {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Success"
        alert.headerText = null
        alert.contentText = message
        alert.showAndWait()
    }

    @JvmStatic
    fun showErrorDialog(errorMessage: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = "Error"
        alert.headerText = null
        alert.contentText = errorMessage
        alert.showAndWait()
    }

    @JvmStatic
    fun refreshService(function: () -> Unit) {
        object : ScheduledService<Unit>() {

            init {
                period = Duration.minutes(1.0)
            }

            override fun createTask(): Task<Unit> {
                return object : Task<Unit>() {
                    override fun call() {
                        function()
                    }
                }
            }
        }.start()
    }

}

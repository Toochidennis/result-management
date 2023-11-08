package com.toochi.resultmanagement.utils

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXMLLoader
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
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

}

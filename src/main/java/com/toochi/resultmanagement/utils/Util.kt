package com.toochi.resultmanagement.utils

import javafx.collections.FXCollections
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
    fun generateSessions(): List<String> {
        return (2005..2030)
            .map(Year::of)
            .map(Year::toString)
            .toCollection(ArrayList())
    }

    @JvmStatic
    fun generateSemesters() = FXCollections.observableArrayList(
        "First semester",
        "Second semester"
    )

    @JvmStatic
    fun generateProgrammes()= FXCollections.observableArrayList("PGD", "MBA","MSC")

}

package com.toochi.resultmanagement.utils

import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.BorderPane
import java.lang.Exception

object Util {

    @JvmStatic
    fun loadFXMLScene(root: BorderPane, fxmlFileName: String) {
        try {
            val loader = FXMLLoader(javaClass.getResource("/com/toochi/resultmanagement/$fxmlFileName"))
            val layout = loader.load<AnchorPane>()
            root.center = layout
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

package com.toochi.resultmanagement.utils

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.concurrent.ScheduledService
import javafx.concurrent.Task
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.effect.BoxBlur
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.util.Duration

object Util {

    @JvmStatic
    fun loadFXMLScene(root: BorderPane, fxmlFileName: String) {
        try {
            val loader =
                FXMLLoader(javaClass.getResource("/com/toochi/resultmanagement/$fxmlFileName"))
            val layout = loader.load<Node>()
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
    fun showMessageDialog(
        rootPane: StackPane,
        nodeToBlur: Node,
        controls: List<JFXButton>,
        header: String,
        body: String = ""
    ) {
        val blurEffect = BoxBlur(3.0, 3.0, 3)
        nodeToBlur.effect = blurEffect

        val jfxDialogLayout = JFXDialogLayout()
        val jfxDialog = JFXDialog(rootPane, jfxDialogLayout, JFXDialog.DialogTransition.TOP)

        jfxDialog.setOnDialogClosed {
            nodeToBlur.effect = null
        }

        controls.forEach { jfxButton ->
            jfxButton.styleClass.add("dialog-button")
            jfxButton.setOnAction { jfxDialog.close() }
        }

        with(jfxDialogLayout) {
            setActions(controls)
            setHeading(Label(header).apply {
                styleClass.add("dialog-header")
            })
            setBody(Label(body).apply {
                styleClass.add("dialog-body")
            })
        }

        jfxDialog.show()
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

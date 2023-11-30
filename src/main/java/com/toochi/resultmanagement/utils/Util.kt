package com.toochi.resultmanagement.utils

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXDialog
import com.jfoenix.controls.JFXDialogLayout
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.embed.swing.SwingFXUtils
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.control.Label
import javafx.scene.effect.BoxBlur
import javafx.scene.image.WritableImage
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane
import javafx.stage.FileChooser
import javafx.stage.Window
import javafx.util.Duration
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory
import java.io.File

object Util {

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

    fun generateSessions(): ObservableList<String> {
        val sessionList = FXCollections.observableArrayList<String>()

        for (i in 2005..2030) {
            sessionList.add("${i - 1}/$i")
        }

        return sessionList
    }


    fun generateSemesters(): ObservableList<String> =
        FXCollections.observableArrayList(
            "First semester",
            "Second semester"
        )


    fun generateProgrammes(): ObservableList<String> =
        FXCollections.observableArrayList(
            "Postgraduate diploma (PGD)",
            "Masters",
            "Doctor of Philosophy (Ph.D)"
        )


    fun generateGender(): ObservableList<String> =
        FXCollections.observableArrayList("Male", "Female", "Prefer not to say")


    fun showMessageDialog(
        rootPane: StackPane,
        nodeToBlur: Node,
        controls: List<Pair<JFXButton, (() -> Unit)?>>,
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

        controls.forEach { (jfxButton, action) ->
            jfxButton.styleClass.add("dialog-button")
            jfxButton.setOnAction {
                jfxDialog.close()
                action?.invoke()
            }


        }

        with(jfxDialogLayout) {
            setActions(controls.map { it.first })
            setHeading(Label(header).apply {
                styleClass.add("dialog-header")
            })
            setBody(Label(body).apply {
                styleClass.add("dialog-body")
            })
        }

        jfxDialog.show()
    }

    fun refreshService(function: () -> Unit) {
        val timeLine = Timeline(
            KeyFrame(
                Duration.seconds(1.0),
                {
                    function.invoke()
                }
            )
        )

        timeLine.cycleCount = Timeline.INDEFINITE
        timeLine.play()

        /* object : ScheduledService<Unit>() {

             init {
                 period = Duration.seconds(15.0)
             }

             override fun createTask(): Task<Unit> {
                 return object : Task<Unit>() {
                     override fun call() {
                         function()
                     }
                 }
             }
         }.start()*/
    }

    fun openFileChooser(
        window: Window,
        filters: FileChooser.ExtensionFilter,
        directory: String,
        title: String
    ): File? {
        val fileChooser = FileChooser().apply {
            this.title = title
            extensionFilters.add(filters)
        }

        return if (directory == "Pictures") {
            fileChooser.showOpenDialog(window)
        } else {
            fileChooser.initialDirectory = File(System.getProperty("user.home"), directory)
            fileChooser.showSaveDialog(window)
        }
    }

    /* fun generatePdf(root: Node, filePath: String) {
         val document = PDDocument()

         // Set page dimensions to A4 size (8.27 x 11.69 inches)
         val pageWidth = PDRectangle.A4.width
         val pageHeight = PDRectangle.A4.height
         val page = PDPage(PDRectangle(pageWidth, pageHeight))

         document.addPage(page)

         val contentStream = PDPageContentStream(document, page)

         val snapshot = SnapshotParameters()
         val scaleX = pageWidth / root.boundsInLocal.width
         val scaleY = pageHeight / root.boundsInLocal.height

         val writableImage = WritableImage(pageWidth.toInt(), pageHeight.toInt())
         snapshot.transform = Transform.scale(scaleX, scaleY)
         root.snapshot(snapshot, writableImage)

         val bufferedImage = SwingFXUtils.fromFXImage(writableImage, null)
         val pdImageXObject = LosslessFactory.createFromImage(document, bufferedImage)

         contentStream.drawImage(pdImageXObject, 0f, 0f, pageWidth, pageHeight)

         contentStream.close()

         document.save(filePath)
         document.close()
     }*/


    fun generatePdf(root: Node, filePath: String) {
        val document = PDDocument()
        val page = PDPage()

        // Set page dimensions to fit the entire content
        val pageWidth = root.boundsInLocal.width
        val pageHeight = root.boundsInLocal.height
        page.mediaBox = PDRectangle(pageWidth.toFloat(), pageHeight.toFloat())

        document.addPage(page)

        val contentStream = PDPageContentStream(document, page)

        val snapshot = SnapshotParameters()
        val writableImage = WritableImage(pageWidth.toInt(), pageHeight.toInt())
        root.snapshot(snapshot, writableImage)

        val bufferedImage = SwingFXUtils.fromFXImage(writableImage, null)
        val pdImageXObject = LosslessFactory.createFromImage(document, bufferedImage)

        contentStream.drawImage(pdImageXObject, 0f, 0f, pageWidth.toFloat(), pageHeight.toFloat())

        contentStream.close()

        document.save(filePath)
        document.close()
    }
}

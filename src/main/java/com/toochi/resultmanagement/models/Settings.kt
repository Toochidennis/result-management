package com.toochi.resultmanagement.models

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty

class Settings(
    val serialNumber: Int,
    val courseId: Int,
    val courseName: String,
    val courseCode: String,
    val courseUnits: Int,
    val programme: String = "",
    val semester: String = "",
    gradeLater: String = "",
    gradePoint: Int = 0,
    total: Double = 0.0
) {
    val gradeLaterProperty = SimpleStringProperty(gradeLater)
    val gradePointProperty = SimpleIntegerProperty(gradePoint)
    val totalProperty = SimpleDoubleProperty(total)

    init {
        gradeLaterProperty.addListener { _, _, _ ->
            calculateGradePointAndTotal()
        }
    }

    private fun calculateGradePointAndTotal() {
        when (gradeLaterProperty.value) {
            "A" -> gradePointProperty.value = 5
            "B" -> gradePointProperty.value = 4
            "C" -> gradePointProperty.value = 3
            else -> gradePointProperty.value = 0
        }

        totalProperty.set(gradePointProperty.get() * courseUnits.toDouble())
    }


}
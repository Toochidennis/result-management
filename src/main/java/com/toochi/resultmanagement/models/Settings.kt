package com.toochi.resultmanagement.models

import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty

class Settings(
    val serialNumber: Int,
    val courseId: Int,
    val courseName: String,
    val courseCode: String,
    val courseUnits: Int,
    val programme: String = "",
    val semester: String = ""
) {
    val gradeLaterProperty = SimpleStringProperty()
    val gradePointProperty = SimpleDoubleProperty()
    val totalProperty = SimpleDoubleProperty()

    init {
        gradeLaterProperty.addListener { _, _, _ ->
            calculateGradePointAndTotal()
        }
    }

    private fun calculateGradePointAndTotal() {
        when (gradeLaterProperty.value) {
            "A" -> gradePointProperty.value = 5.0
            "B" -> gradePointProperty.value = 4.0
            "C" -> gradePointProperty.value = 3.0
            else -> gradePointProperty.value = 0.0
        }

        totalProperty.set(gradePointProperty.get() * courseUnits)
    }


}
package com.toochi.resultmanagement.models

data class Student(
    val studentId: String,
    val firstName: String,
    val middleName: String,
    val lastName: String,
    val matricNumber:String,
    val phoneNumber: String,
    val discipline: String,
    val session: String,
    val programme: String,
    val dateOfBirth: String,
    val email: String,
    val address: String
)
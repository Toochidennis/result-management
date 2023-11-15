package com.toochi.resultmanagement.models

data class Student(
    val studentId: Int,
    val surname: String,
    val firstName: String,
    val otherName: String,
    val registrationNumber: String,
    val phoneNumber: String,
    val discipline: String,
    val session: String,
    val programme: String,
    val dateOfBirth: String,
    val email: String,
    val address: String,
    val passport: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Student

        if (studentId != other.studentId) return false
        if (surname != other.surname) return false
        if (firstName != other.firstName) return false
        if (otherName != other.otherName) return false
        if (registrationNumber != other.registrationNumber) return false
        if (phoneNumber != other.phoneNumber) return false
        if (discipline != other.discipline) return false
        if (session != other.session) return false
        if (programme != other.programme) return false
        if (dateOfBirth != other.dateOfBirth) return false
        if (email != other.email) return false
        if (address != other.address) return false
        if (passport != null) {
            if (other.passport == null) return false
            if (!passport.contentEquals(other.passport)) return false
        } else if (other.passport != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = studentId
        result = 31 * result + surname.hashCode()
        result = 31 * result + firstName.hashCode()
        result = 31 * result + otherName.hashCode()
        result = 31 * result + registrationNumber.hashCode()
        result = 31 * result + phoneNumber.hashCode()
        result = 31 * result + discipline.hashCode()
        result = 31 * result + session.hashCode()
        result = 31 * result + programme.hashCode()
        result = 31 * result + dateOfBirth.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + (passport?.contentHashCode() ?: 0)
        return result
    }
}
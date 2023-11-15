package com.toochi.resultmanagement.backend

import java.util.Properties

class Config {
    private val properties = Properties()

    init {
        javaClass.classLoader.getResourceAsStream("com/toochi/resultmanagement/config/config.properties").use {
            properties.load(it)
        }
    }

    val dbUrl: String by properties
    val dbUser: String by properties
    val dbPassword: String by properties
    val dbDatabase: String by properties
    val dbSettingsTable: String by properties
    val dbStudentsTable: String by properties
    val dbResultsTable: String by properties

}
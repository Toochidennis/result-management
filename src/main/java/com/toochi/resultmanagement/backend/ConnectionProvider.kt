package com.toochi.resultmanagement.backend

import java.sql.Connection
import java.sql.DriverManager

class ConnectionProvider(config: Config) {

    // Configurations
    private val url = config.dbUrl
    private val user = config.dbUser
    private val password = config.dbPassword
    private val databaseName = config.dbDatabase
    private val settingsTable = config.dbSettingsTable
    private val studentsTable = config.dbStudentsTable
    private val resultsTable = config.dbResultsTable


    // SQL statements for creating database and tables
    private val createDatabaseSQL = "CREATE DATABASE IF NOT EXISTS $databaseName"

    private val createSettingsTableSQL = """
        CREATE TABLE IF NOT EXISTS $databaseName.$settingsTable (   
            course_id INT AUTO_INCREMENT PRIMARY KEY,
            course_name VARCHAR(255),
            course_code VARCHAR(10),
            course_units INT,
            programme VARCHAR(25),
            semester VARCHAR(25)
        );
    """

    private val createStudentsTableSQL = """
        CREATE TABLE IF NOT EXISTS $databaseName.$studentsTable (
            student_id INT AUTO_INCREMENT PRIMARY KEY,
            first_name VARCHAR(255),
            middle_name VARCHAR(255),
            last_name VARCHAR(255),
            session VARCHAR(25),
            programme VARCHAR(25),
            discipline VARCHAR(255),
            date_of_birth VARCHAR(255),
            phone_number VARCHAR(25),
            email VARCHAR(255),
            address VARCHAR(255)
        );
    """

    private val createResultsTableSQL = """
        CREATE TABLE IF NOT EXISTS $databaseName.$resultsTable (
            result_id INT AUTO_INCREMENT PRIMARY KEY,
            student_id INT,
            course_id INT,
            grade_later VARCHAR(2),
            grade_point DOUBLE,
            total DOUBLE,
            
            FOREIGN KEY (student_id) REFERENCES $databaseName.$studentsTable(student_id),
            FOREIGN KEY (course_id) REFERENCES $databaseName.$settingsTable(course_id)
        );
    """

    fun getConnection(): Connection? {
        return try {
            val connection = DriverManager.getConnection(url, user, password)
            val queries =
                listOf(createDatabaseSQL, createSettingsTableSQL, createStudentsTableSQL, createResultsTableSQL)

            connection.run {
                for (query in queries) {
                    prepareStatement(query).use { preparedStatement ->
                        preparedStatement.executeUpdate()
                    }
                }
                prepareStatement("USE $databaseName").executeUpdate()
            }

            connection
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
package com.toochi.resultmanagement.backend

import java.sql.Connection
import java.sql.DriverManager

/**
 * Package Documentation: `com.toochi.resultmanagement.backend`
 *
 * Overview
 * ------------
 * The `com.toochi.resultmanagement.backend` package includes a `ConnectionProvider` class responsible for managing
 * database connections and initializing the required database and tables.
 *
 * Classes and Methods
 * --------------------------
 *
 * ### `ConnectionProvider`
 *
 * This class manages database connections and initializes the necessary database and tables.
 *
 * #### Constructor:
 *
 * - **`ConnectionProvider(config: Config)`**
 *   - Purpose: Constructs a `ConnectionProvider` instance with the provided configuration.
 *   - Parameters:
 *     - `config` (Config): An instance of the `Config` class containing database configuration parameters.
 *
 * #### Methods:
 *
 * 1. **`getConnection`**
 *    - Purpose: Establishes a connection to the database and initializes the database and tables if not already created.
 *    - Returns: A `Connection` object representing the established database connection or `null` if an error occurs.
 *
 * Usage Example
 * -------------------
 *
 * #### Creating a Database Connection
 *
 * ```kotlin
 * val config = Config()
 * val connectionProvider = ConnectionProvider(config)
 * val connection = connectionProvider.getConnection()
 * ```
 *
 * Note
 * --------
 * - The `Config` class should be properly configured with the necessary database connection details before using the `ConnectionProvider`.
 * - The `createDatabaseSQL`, `createSettingsTableSQL`, `createStudentsTableSQL`, and `createResultsTableSQL` properties
 *   contain SQL statements for creating the database and tables if they do not already exist.
 * - The `getConnection` method ensures that the necessary database and tables are created before returning the connection.
 * - Ensure that the database connection details and SQL statements are appropriately configured for your application.
 */

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
            surname VARCHAR(255),
            first_name VARCHAR(255),
            other_name VARCHAR(255),
            registration_number VARCHAR(25),
            phone_number VARCHAR(20),
            date_of_birth VARCHAR(25),
            address VARCHAR(255),
            email VARCHAR(255),
            gender VARCHAR(20),
            passport LONGBLOB,
            next_of_kin_name VARCHAR(255),
            next_of_kin_phone_number VARCHAR(20),
            next_of_kin_address VARCHAR(255),
            session VARCHAR(25),
            programme VARCHAR(255),
            discipline VARCHAR(255),
            supervisor VARCHAR(255)
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
                listOf(
                    createDatabaseSQL,
                    createSettingsTableSQL,
                    createStudentsTableSQL,
                    createResultsTableSQL
                )

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
package com.toochi.resultmanagement.backend

import java.io.FileInputStream
import java.nio.file.Path
import java.sql.PreparedStatement
import java.sql.ResultSet


/**
 * Package Documentation: `com.toochi.resultmanagement.backend`
 *
 * Overview
 * ------------
 * The `com.toochi.resultmanagement.backend` package provides functionality for executing various SQL queries
 * and interacting with the database. It includes methods for inserting records, updating records, and executing
 * select queries with conditions. Additionally, there are methods for executing specific queries related to
 * retrieving student information with associated results.
 *
 * Classes and Methods
 * --------------------------
 *
 * ### `QueryExecutor`
 *
 * This class serves as a utility for executing SQL queries and interacting with the database.
 *
 * #### Methods:
 *
 * 1. **`executeInsertQuery`**
 *    - Purpose: Executes an insert query on the specified table with the provided values.
 *    - Parameters:
 *      - `tableName` (String): The name of the table to insert into.
 *      - `values` (HashMap<String, Any>): A map containing column names and their corresponding values to be inserted.
 *    - Returns: The number of affected rows.
 *
 * 2. **`executeSelectWithConditionsQuery2`**
 *    - Purpose: Executes a select query with conditions on the specified table.
 *    - Parameters:
 *      - `tableName` (String): The name of the table to query.
 *      - `conditions` (HashMap<String, Any>): A map containing column names and their corresponding values for conditions.
 *    - Returns: A `ResultSet` containing the query results.
 *
 * 3. **`executeSelectAllQuery`**
 *    - Purpose: Executes a select all query on the specified table.
 *    - Parameters:
 *      - `tableName` (String): The name of the table to query.
 *    - Returns: A `ResultSet` containing the query results.
 *
 * 4. **`executeUpdateQuery`**
 *    - Purpose: Executes an update query on the specified table with the provided values and conditions.
 *    - Parameters:
 *      - `tableName` (String): The name of the table to update.
 *      - `values` (HashMap<String, Any>): A map containing column names and their corresponding values to be updated.
 *      - `conditions` (HashMap<String, Any>): A map containing column names and their corresponding values for conditions.
 *    - Returns: The number of affected rows.
 *
 * 5. **`executeAllTablesQuery`**
 *    - Purpose: Executes a complex query to retrieve data from multiple tables related to a specific student.
 *    - Parameters:
 *      - `studentId` (Int): The ID of the student for whom to retrieve data.
 *    - Returns: A `ResultSet` containing the query results.
 *
 * 6. **`executeStudentsWithResultQuery`**
 *    - Purpose: Executes a query to retrieve distinct student records with associated results.
 *    - Parameters:
 *      - `tableName` (String): The name of the table representing students.
 *    - Returns: A `ResultSet` containing the query results.
 *
 * 7. **`setParameters`**
 *    - Purpose: Sets parameters on a prepared statement based on the provided values.
 *    - Parameters:
 *      - `preparedStatement` (PreparedStatement): The prepared statement to set parameters on.
 *      - `parameters` (MutableList<Any>): A list of parameters to be set on the prepared statement.
 *
 * Usage Examples
 * -------------------
 *
 * #### Inserting Records
 *
 * ```kotlin
 * val values = hashMapOf(
 *     "column1" to value1,
 *     "column2" to value2,
 *     // Add additional columns and values as needed
 * )
 *
 * val affectedRows = QueryExecutor.executeInsertQuery("tableName", values)
 * ```
 *
 * #### Selecting Records with Conditions
 *
 * ```kotlin
 * val conditions = hashMapOf(
 *     "column1" to value1,
 *     "column2" to value2,
 *     // Add additional columns and values as needed
 * )
 *
 * val resultSet = QueryExecutor.executeSelectWithConditionsQuery2("tableName", conditions)
 * ```
 *
 * #### Updating Records
 *
 * ```kotlin
 * val values = hashMapOf(
 *     "column1" to newValue1,
 *     "column2" to newValue2,
 *     // Add additional columns and values as needed
 * )
 *
 * val conditions = hashMapOf(
 *     "conditionColumn" to conditionValue
 * )
 *
 * val affectedRows = QueryExecutor.executeUpdateQuery("tableName", values, conditions)
 * ```
 *
 * #### Retrieving Data from Multiple Tables
 *
 * ```kotlin
 * val studentId = 123
 * val resultSet = QueryExecutor.executeAllTablesQuery(studentId)
 * ```
 *
 * #### Retrieving Distinct Students with Results
 *
 * ```kotlin
 * val tableName = "students"
 * val resultSet = QueryExecutor.executeStudentsWithResultQuery(tableName)
 * ```
 *
 * Note
 * --------
 * - Ensure that the database connection is properly configured using the `Config` and `ConnectionProvider` classes.
 * - The examples provided cover common use cases, and you can adapt them to suit the specific requirements of your application.
 * - Use the appropriate methods based on the type of query you want to execute (insert, select, update) and whether conditions are involved.
 */

object QueryExecutor {

    private fun connection() = ConnectionProvider(Config()).getConnection()
    private var resultSet: ResultSet? = null

    @JvmStatic
    fun executeInsertQuery(tableName: String, values: HashMap<String, Any>): Int {
        val sql = generateInsertQuery(tableName, values)
        var affectedRows = 0

        connection()?.use { conn ->
            conn.prepareStatement(sql, ResultSet.CONCUR_UPDATABLE).use {
                setParameters(it, values.values.toMutableList())
                affectedRows = it.executeUpdate()
            }
        }

        return affectedRows
    }

    @JvmStatic
    fun executeSelectWithConditionsQuery2(
        tableName: String,
        conditions: HashMap<String, Any>
    ): ResultSet? {
        val sql = generateSelectQueryById(tableName, conditions)

        connection()?.use { conn ->
            conn.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY).use {
                setParameters(it, conditions.values.toMutableList())
                resultSet = it.executeQuery()
            }
        }

        return resultSet
    }


    @JvmStatic
    fun executeSelectAllQuery(tableName: String): ResultSet? {
        val sql = "SELECT * FROM $tableName"

        connection()?.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY)?.use {
            resultSet = it.executeQuery()
        }

        return resultSet
    }

    @JvmStatic
    fun executeUpdateQuery(
        tableName: String,
        values: HashMap<String, Any>,
        conditions: HashMap<String, Any>
    ): Int {
        val sql = generateUpdateQuery(tableName, values, conditions)
        var affectedRows = 0
        val allParameters = values.values.toMutableList() + conditions.values.toMutableList()

        connection()?.use { conn ->
            conn.prepareStatement(sql, ResultSet.CONCUR_UPDATABLE)?.use {
                setParameters(it, allParameters.toMutableList())
                affectedRows = it.executeUpdate()
            }

        }
        return affectedRows
    }

    @JvmStatic
    fun executeAllTablesQuery(studentId: Int): ResultSet? {
        val sql = generateAllTablesQuery()

        connection()?.use { conn ->
            conn.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY)?.use {
                setParameters(it, mutableListOf(studentId))
                resultSet = it.executeQuery()
            }
        }

        return resultSet
    }

    @JvmStatic
    fun executeStudentsWithResultQuery(tableName: String): ResultSet? {
        val sql = generateStudentsWithResultQuery(tableName)

        connection()?.use { conn ->
            conn.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY)?.use {
                resultSet = it.executeQuery()
            }
        }

        return resultSet
    }


    @JvmStatic
    fun searchStudentByLastDigits(lastDigits: String): ResultSet? {
        val sql = generateSearchQuery()
        val likePattern = "%/$lastDigits"

        connection()?.use { conn ->
            conn.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY).use {
                setParameters(it, mutableListOf(likePattern))
                resultSet = it.executeQuery()
            }
        }

        return resultSet
    }

    private fun generateSearchQuery(): String {
        return "SELECT * FROM students WHERE registration_number LIKE ?"
    }

    private fun generateInsertQuery(tableName: String, values: HashMap<String, Any>): String {
        val columns = values.keys.joinToString(", ") { it }
        val placeHolders = values.keys.joinToString(", ") { "?" }
        return "INSERT INTO $tableName ($columns) VALUES ($placeHolders)"
    }

    private fun generateSelectQueryById(
        tableName: String,
        conditions: HashMap<String, Any>
    ): String {
        val whereClause = conditions.keys.joinToString(" AND ") { "$it = ?" }
        return "SELECT * FROM $tableName WHERE $whereClause"
    }

    private fun generateUpdateQuery(
        tableName: String,
        values: HashMap<String, Any>,
        conditions: HashMap<String, Any>
    ): String {
        val setClause = values.keys.joinToString(", ") { "$it = ?" }
        val whereClause = conditions.keys.joinToString(" AND ") { "$it = ?" }
        return "UPDATE $tableName SET $setClause WHERE $whereClause"
    }

    private fun generateAllTablesQuery(): String {
        return """
            SELECT students.*, results.*, settings.*
            FROM students
            JOIN results ON students.student_id = results.student_id
            JOIN settings ON results.course_id = settings.course_id
            WHERE students.student_id = ?
        """.trimIndent()
    }

    private fun generateStudentsWithResultQuery(tableName: String): String {
        return """
            SELECT DISTINCT $tableName.*
            FROM students
            INNER JOIN results ON students.student_id = results.student_id
        """.trimIndent()
    }

    private fun setParameters(preparedStatement: PreparedStatement, parameters: MutableList<Any>) {
        parameters.forEachIndexed { index, parameter ->
            if (parameter is Path) {
                val imageStream = FileInputStream(parameter.toFile())
                preparedStatement.setBinaryStream(
                    index + 1,
                    imageStream,
                    imageStream.available().toLong()
                )
            } else {
                preparedStatement.setObject(index + 1, parameter)
            }

        }
    }

}
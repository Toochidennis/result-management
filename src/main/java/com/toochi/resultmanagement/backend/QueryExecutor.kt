package com.toochi.resultmanagement.backend

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

object QueryExecutor {

    private fun connection() = ConnectionProvider(Config()).getConnection()

    @JvmStatic
    fun executeInsertQuery(tableName: String, values: HashMap<String, Any>): Int {
        val sql = generateInsertQuery(tableName, values)
        var affectedRows = 0

        connection()?.use { conn ->
            conn.prepareStatement(sql).use {
                setParameters(it, values.values.toMutableList())
                affectedRows = it.executeUpdate()
            }
        }

        return affectedRows
    }

    @JvmStatic
    fun executeSelectWithConditionsQuery(
        tableName: String,
        conditions: HashMap<String, Any>
    ): ResultSet? {
        val sql = generateSelectQueryWithConditionsQuery(tableName, conditions)
        var resultSet: ResultSet? = null

        connection()?.use { conn ->
            conn.prepareStatement(sql, ResultSet.CONCUR_READ_ONLY).use {
                setParameters(it, conditions.values.toMutableList())
                resultSet = it.executeQuery()
            }
        }

        return resultSet
    }

    @JvmStatic
    fun executeSelectWithConditionsQuery2(tableName: String, conditions: HashMap<String, Any>): ResultSet? {
        val sql = generateSelectQueryById(tableName, conditions)
        var resultSet: ResultSet? = null

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
        var resultSet: ResultSet? = null
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


    private fun generateInsertQuery(tableName: String, values: HashMap<String, Any>): String {
        val columns = values.keys.joinToString(", ") { it }
        val placeHolders = values.keys.joinToString(", ") { "?" }
        return "INSERT INTO $tableName ($columns) VALUES ($placeHolders);"
    }

    private fun generateSelectQueryWithConditionsQuery(
        tableName: String,
        conditions: HashMap<String, Any>
    ): String {
        val columns = conditions.keys.joinToString { ", " }
        val whereClause = conditions.keys.joinToString(" AND ") { "$it = ?" }
        return "SELECT $columns FROM $tableName WHERE $whereClause;"
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
        val whereClause = conditions.keys.joinToString(", ") { "$it = ?" }
        return "UPDATE $tableName SET $setClause WHERE $whereClause"
    }

    private fun setParameters(preparedStatement: PreparedStatement, parameters: MutableList<Any>) {
        parameters.forEachIndexed { index, parameter ->
            preparedStatement.setObject(index + 1, parameter)
        }
    }

}
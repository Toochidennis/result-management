package com.toochi.resultmanagement.backend

import java.sql.ResultSet

class QueryExecutor() {

    private var connectionProvider: ConnectionProvider? = null

    init {
        connectionProvider = ConnectionProvider(Config())
    }

    fun executeQuery(sql: String): ResultSet? {
        var resultSet: ResultSet? = null
        connectionProvider?.getConnection()?.run {
            try {
                val preparedStatement =
                    prepareStatement(sql, ResultSet.CONCUR_READ_ONLY, ResultSet.TYPE_SCROLL_INSENSITIVE)
                resultSet = preparedStatement.executeQuery()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                close()
            }
        }

        return resultSet
    }
}
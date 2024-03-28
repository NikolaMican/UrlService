package com.spt.urls.db.user

import com.spt.urls.db.BaseDbController
import com.spt.urls.db.HikariService
import java.sql.ResultSet
import java.sql.Statement

data class UserBean(var idUser: Int = 0, var username: String, var apiKey: String, var isTestUser: Boolean) {
    constructor(username: String, apiKey: String, isTestUser: Boolean): this(0, username = username, apiKey = apiKey, isTestUser = isTestUser)
}

class UserDbController(
    hikariService: HikariService
): BaseDbController(hikariService) {
    private val TABLE_NAME= "user"

    private val COLUMN_ID_USER = "id_user"
    private val COLUMN_USERNAME = "username"
    private val COLUMN_API_KEY = "api_key"
    private val COLUMN_IS_TEST_USER = "is_test_user"

    private val QUERY_INSERT = "INSERT INTO user(username, api_key, is_test_user) VALUES (?,?,?)"
    private val QUERY_EDIT = "UPDATE user SET username=?, api_key=?, is_test_user=? WHERE id_user=?"
    private val QUERY_GET_BY_USERNAME = "SELECT * FROM user WHERE username=?"
    private val QUERY_GET_BY_API_KEY = "SELECT * FROM user WHERE api_key=?"


    fun insert(user: UserBean) {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_INSERT, Statement.RETURN_GENERATED_KEYS).apply {
                setString(1, user.username)
                setString(2, user.apiKey)
                setBoolean(3, user.isTestUser)
            }.use { ps ->
                ps.executeUpdate()
                ps.generatedKeys.use { rs ->
                    rs.next()
                    user.idUser = rs.getInt(1)  // get autoincrement key
                }
            }
        }
    }

    fun edit(user: UserBean) {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_EDIT).apply {
                setString(1, user.username)
                setString(2, user.apiKey)
                setBoolean(3, user.isTestUser)
                setInt(4, user.idUser)
            }.use { ps ->
                ps.executeUpdate()
            }
        }
    }

    fun delete(user: UserBean) = delete(TABLE_NAME, user.idUser)

    fun getByUsername(username: String): UserBean? {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_GET_BY_USERNAME).apply {
                setString(1, username)
            }.use { ps ->
                ps.executeQuery().use { rs ->
                    return getUser(rs)
                }
            }
        }
    }

    fun getByApiKey(apiKey: String): UserBean? {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_GET_BY_API_KEY).apply {
                setString(1, apiKey)
            }.use { ps ->
                ps.executeQuery().use { rs ->
                    return getUser(rs)
                }
            }
        }
    }

    private fun getUser(rs: ResultSet): UserBean? {
        if (rs.next()) {
            val id = rs.getInt(COLUMN_ID_USER)
            val username = rs.getString(COLUMN_USERNAME)
            val apiKey = rs.getString(COLUMN_API_KEY)
            val isTestUser = rs.getBoolean(COLUMN_IS_TEST_USER)
            return UserBean(id, username, apiKey, isTestUser)
        }
        return null
    }
}
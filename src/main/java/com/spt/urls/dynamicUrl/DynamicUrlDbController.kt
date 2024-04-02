
package com.spt.urls.dynamicUrl

import com.spt.urls.db.BaseDbController
import com.spt.urls.db.HikariService
import java.sql.ResultSet
import java.sql.Statement


data class DynamicUrlBean(var idDynamicUrl: Int = 0, var fkIdUser: Int, var dynamicUrlTemplate: String, var urlId: String, var redirectUrl: String, var numOfClicks: Int) {
    constructor(fkIdUser: Int, dynamicUrlTemplate: String, urlId: String, redirectUrl: String, numOfClicks: Int): this(0, fkIdUser = fkIdUser, dynamicUrlTemplate = dynamicUrlTemplate, urlId = urlId, redirectUrl = redirectUrl, numOfClicks = numOfClicks)
}

class DynamicUrlDbController(
    hikariService: HikariService
): BaseDbController(hikariService) {
    private val TABLE_NAME= "dynamic_url"

    private val COLUMN_ID_DYNAMIC_URL = "id_dynamic_url"
    private val COLUMN_FK_ID_USER = "fk_id_user"
    private val COLUMN_DYNAMIC_URL_TEMPLATE = "dynamic_url_template"
    private val COLUMN_URL_ID = "url_id"
    private val COLUMN_REDIRECT_URL = "redirect_url"
    private val COLUMN_NUM_OF_CLICKS = "num_of_clicks"

    private val QUERY_INSERT = "INSERT INTO dynamic_url(fk_id_user, dynamic_url_template, url_id, redirect_url, num_of_clicks) VALUES (?,?,?,?,?)"
    private val QUERY_EDIT = "UPDATE dynamic_url SET fk_id_user=?, dynamic_url_template=?, url_id=?, redirect_url=?, num_of_clicks=? WHERE id_dynamic_url=?"
    private val QUERY_GET_BY_URL_ID = "SELECT * FROM dynamic_url WHERE url_id=?"
    private val QUERY_GET_BY_USER_ID_AND_URL_ID = "SELECT * FROM dynamic_url WHERE fk_id_user=? and url_id=?"



    fun insert(b: DynamicUrlBean) {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_INSERT, Statement.RETURN_GENERATED_KEYS).apply {
                setInt(1, b.fkIdUser)
                setString(2, b.dynamicUrlTemplate)
                setString(3, b.urlId)
                setString(4, b.redirectUrl)
                setInt(5, b.numOfClicks)
            }.use { ps ->
                ps.executeUpdate()
                ps.generatedKeys.use { rs ->
                    rs.next()
                    b.idDynamicUrl = rs.getInt(1)  // get autoincrement key
                }
            }
        }
    }

    fun edit(b: DynamicUrlBean) {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_EDIT).apply {
                setInt(1, b.fkIdUser)
                setString(2, b.dynamicUrlTemplate)
                setString(3, b.urlId)
                setString(4, b.redirectUrl)
                setInt(5, b.numOfClicks)
                setInt(6, b.idDynamicUrl)
            }.use { ps ->
                ps.executeUpdate()
            }
        }
    }

    fun delete(b: DynamicUrlBean) = delete(TABLE_NAME, b.idDynamicUrl)

    fun get(userId: Int, urlId: String): DynamicUrlBean? {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_GET_BY_USER_ID_AND_URL_ID).apply {
                setInt(1, userId)
                setString(2, urlId)
            }.use { ps ->
                ps.executeQuery().use { rs ->
                    return getDynamicUrlBean(rs)
                }
            }
        }
    }

    fun get(urlId: String): DynamicUrlBean? {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_GET_BY_URL_ID).apply {
                setString(1, urlId)
            }.use { ps ->
                ps.executeQuery().use { rs ->
                    return getDynamicUrlBean(rs)
                }
            }
        }
    }

    private fun getDynamicUrlBean(rs: ResultSet): DynamicUrlBean? {
        if (rs.next()) {
            val id = rs.getInt(COLUMN_ID_DYNAMIC_URL)
            val fkUserId = rs.getInt(COLUMN_FK_ID_USER)
            val dynamicUrlTemplate = rs.getString(COLUMN_DYNAMIC_URL_TEMPLATE)
            val url = rs.getString(COLUMN_URL_ID)
            val redirect = rs.getString(COLUMN_REDIRECT_URL)
            val numOfClicks = rs.getInt(COLUMN_NUM_OF_CLICKS)
            return DynamicUrlBean(id, fkUserId, dynamicUrlTemplate, url, redirect, numOfClicks)
        }
        return null
    }
}
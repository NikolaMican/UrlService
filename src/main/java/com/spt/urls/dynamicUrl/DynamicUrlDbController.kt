
package com.spt.urls.dynamicUrl

import com.spt.urls.dbConection.BaseDbController
import com.spt.urls.dbConection.HikariService

/**
 *
 * @author Nikola Micanovic
 */
class DynamicUrlDbController(
    hikariService: HikariService
): BaseDbController(hikariService) {
    private val TABLE_NAME= "dynamic_url"

    private val COLUMN_ID_DYNAMIC_URL = "id_dynamic_url"
    private val COLUMN_URL_ID = "url_id"
    private val COLUMN_REDIRECT_URL = "redirect_url"
    private val COLUMN_NUM_OF_CLICKS = "num_of_clicks"

    private val QUERY_INSERT = "INSERT INTO dynamic_url(url_id, redirect_url, num_of_clicks) VALUES (?,?,?)"
    private val QUERY_EDIT = "UPDATE dynamic_url SET url_id=?, redirect_url=?, num_of_clicks=? WHERE id_dynamic_url=?"
    private val QUERY_GET_ALL = "SELECT * FROM dynamic_url WHERE url_id=?"


    fun insert(b: DynamicUrlBean) {
        hikariService.getHikariInstance().connection.use {
            synchronized(QUERY_INSERT) {
                val ps = it.prepareStatement(QUERY_INSERT).apply {
                    setString(1, b.urlId)
                    setString(2, b.redirectUrl)
                    setInt(3, b.numOfClicks)
                }
                ps.executeUpdate()
                val id = getLastId(TABLE_NAME)
                b.idDynamicUrl = id
            }
        }
    }

    fun edit(b: DynamicUrlBean) {
        hikariService.getHikariInstance().connection.use {
            val ps = it.prepareStatement(QUERY_EDIT).apply {
                setString(1, b.urlId)
                setString(2, b.redirectUrl)
                setInt(3, b.numOfClicks)
                setInt(4, b.idDynamicUrl)
            }
            ps.executeUpdate()
            ps.close()
        }
    }

    fun delete(b: DynamicUrlBean) = delete(TABLE_NAME, b.idDynamicUrl)

    fun get(urlId: String?): DynamicUrlBean? {
        hikariService.getHikariInstance().connection.use {
            val ps = it.prepareStatement(QUERY_GET_ALL).apply {
                setString(1, urlId)
            }

            val rs = ps.executeQuery()
            if (rs.next()) {
                val id = rs.getInt(COLUMN_ID_DYNAMIC_URL)
                val url = rs.getString(COLUMN_URL_ID)
                val redirect = rs.getString(COLUMN_REDIRECT_URL)
                val numOfClicks = rs.getInt(COLUMN_NUM_OF_CLICKS)
                return DynamicUrlBean(id, url, redirect, numOfClicks)
            }
            rs.close()
            return null
        }
    }
}
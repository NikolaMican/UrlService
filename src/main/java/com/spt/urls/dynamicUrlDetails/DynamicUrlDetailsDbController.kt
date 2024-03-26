
package com.spt.urls.dynamicUrlDetails

import com.spt.urls.dbConection.BaseDbController
import com.spt.urls.dbConection.HikariService
import java.sql.Statement
import java.sql.Timestamp

/**
 *
 * @author Nikola Micanovic
 */
class DynamicUrlDetailsDbController(
    hikariService: HikariService
): BaseDbController(hikariService) {
    private val TABLE_NAME= "dynamic_url_details"

    private val COLUMN_FK_ID_DYNAMIC_URL_DETAILS = "fk_id_dynamic_url"
    private val COLUMN_TIME = "click_time"
    private val COLUMN_LOCATION = "location"
    private val COLUMN_BROWSER = "browser"
    private val COLUMN_PLATFORM = "platform"
    private val COLUMN_IS_MOBILE_PLATFORM = "isMobilePlatform"

    private val QUERY_INSERT = "INSERT INTO dynamic_url_details(fk_id_dynamic_url, click_time, location, browser, platform, isMobilePlatform) VALUES(?,?,?,?,?,?)"
    private val QUERY_EDIT = "UPDATE dynamic_url_details SET fk_id_dynamic_url=?, click_time=?, location=?, browser=?, platform=?, isMobilePlatform=? WHERE id_dynamic_url_details=?"
    private val QUERY_GET_ALL = "SELECT * FROM dynamic_url_details WHERE id_dynamic_url_details=?"

    fun insert(bean: DynamicUrlDetailsBean) {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_INSERT, Statement.RETURN_GENERATED_KEYS).apply {
                setInt(1, bean.fkIdDynamicUrl)
                setTimestamp(2, Timestamp(bean.time))
                setString(3, bean.location)
                setString(4, bean.browser)
                setString(5, bean.platform)
                setBoolean(6, bean.isMobilePlatform)
            }.use { ps ->
                ps.executeUpdate()
                ps.generatedKeys.use { rs ->
                    rs.next()
                    bean.idDynamicUrlDetails = rs.getInt(1)  // get autoincrement key
                }
            }
        }
    }


//    fun edit(b: DynamicUrlDetailsBean) {
//        hikariService.getHikariInstance().connection.use {
//            it.prepareStatement(QUERY_EDIT).apply {
//                setInt(1, b.fkIdDynamicUrl)
//                setTimestamp(2, Timestamp(b.time))
//                setString(3, b.location)
//                setString(4, b.browser)
//                setString(5, b.platform)
//                setInt(6, b.idDynamicUrlDetails)
//            }.use { ps ->
//                ps.executeUpdate()
//            }
//        }
//    }

    fun delete(b: DynamicUrlDetailsBean) = delete(TABLE_NAME, b.idDynamicUrlDetails)

    fun get(id: Int): DynamicUrlDetailsBean? {
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(QUERY_GET_ALL).apply {
                setInt(1, id)
            }.use { ps ->
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        val fkIdDynamicUrlDetails = rs.getInt(COLUMN_FK_ID_DYNAMIC_URL_DETAILS)
                        val time = rs.getTimestamp(COLUMN_TIME)
                        val location = rs.getString(COLUMN_LOCATION)
                        val browser = rs.getString(COLUMN_BROWSER)
                        val platform = rs.getString(COLUMN_PLATFORM)
                        val isMobilePlatform = rs.getBoolean(COLUMN_IS_MOBILE_PLATFORM)
                        return DynamicUrlDetailsBean(id, fkIdDynamicUrlDetails, time.time, location, browser, platform, isMobilePlatform)
                    }
                    return null
                }
            }
        }
    }
}
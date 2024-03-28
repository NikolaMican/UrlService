package com.spt.urls.db

import com.spt.urls.logs.TicketLoggerFactory
import java.sql.SQLException

open class BaseDbController(
    protected val hikariService: HikariService
) {
    private val LOG = TicketLoggerFactory.getTicketLogger(BaseDbController::class.java)

    @Throws(SQLException::class)
    protected fun delete(tableName: String, id: Int) {
        val query = "DELETE FROM $tableName WHERE id_$tableName=?"
        hikariService.getHikariInstance().connection.use {
            it.prepareStatement(query).apply {
                setInt(1, id)
            }.use { ps ->
                ps.executeUpdate()
            }
        }
    }
}
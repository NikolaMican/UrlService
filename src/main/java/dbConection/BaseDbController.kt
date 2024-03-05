package dbConection

import logs.TicketLoggerFactory
import java.sql.SQLException

open class BaseDbController(
    protected val hikariService: HikariService
) {
    private val LOG = TicketLoggerFactory.getTicketLogger(BaseDbController::class.java)

    fun getLastId(tableName: String): Int {
        val query = "SELECT MAX(id_$tableName) FROM $tableName"
        hikariService.getHikariInstance().connection.use {
            val ps = it.prepareStatement(query)
            val rs = ps.executeQuery()
            if (rs.next()) {
                return rs.getInt(1)
            }
            return 0
        }
    }

    @Throws(SQLException::class)
    protected fun delete(tableName: String, id: Int) {
        val query = "DELETE FROM $tableName WHERE id_$tableName=?"
        hikariService.getHikariInstance().connection.use {
            val ps = it.prepareStatement(query).apply {
                setInt(1, id)
            }
            ps.executeUpdate()
        }
    }
}
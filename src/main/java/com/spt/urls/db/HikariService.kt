package com.spt.urls.db

import com.spt.urls.logs.TicketLoggerFactory
import com.spt.urls.CONF_DB_PASS
import com.spt.urls.CONF_DB_PORT
import com.spt.urls.CONF_DB_USERNAME
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

/**
 * NOTE: to enable Hikari logging please uncomment 'Hikari' logger in log4j.xml file
 */
class HikariService {
    private val LOG = TicketLoggerFactory.getTicketLogger(HikariService::class.java)

    private var hikariDataSource: HikariDataSource? = null


    // NOTE: to allow access mysql using 'root' (and EMPTY password) user you have to change auth method. Please
    // visit link for more information: https://stackoverflow.com/questions/41645309/mysql-error-access-denied-for-user-rootlocalhost
    fun getHikariInstance(): HikariDataSource {
        if (hikariDataSource == null) {
            val databaseUrl: String = getDatabaseUrl()
            LOG.debug("setup database.  DatabaseUrl: $databaseUrl")
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = databaseUrl
                username = CONF_DB_USERNAME
                password = CONF_DB_PASS
                minimumIdle = 10
                maximumPoolSize = 20
                connectionTimeout = 5_000   // This property controls the maximum number of milliseconds that a client (that's you) will wait for a connection from the pool.
                addDataSourceProperty("cachePrepStmts", "true")
                addDataSourceProperty("prepStmtCacheSize", "250")
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
//                leakDetectionThreshold = 3000  // Hikari will log query if it takes more the 3s in this case
            }

            hikariDataSource = HikariDataSource(hikariConfig)
        }
        return hikariDataSource!!
    }

    private fun getDatabaseUrl(): String {
        return "jdbc:mysql://localhost:$CONF_DB_PORT/dynamic_url?useUnicode=true&characterEncoding=utf8&useSSL=false"
    }
}
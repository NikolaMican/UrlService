package com.spt.urls.db

import ch.qos.logback.classic.Logger
import com.spt.urls.logs.TicketLoggerFactory
import com.spt.urls.webService.CONF_DB_PASS
import com.spt.urls.webService.CONF_DB_PORT
import com.spt.urls.webService.CONF_DB_USERNAME
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.LoggerFactory

class HikariService {
    private val LOG = TicketLoggerFactory.getTicketLogger(HikariService::class.java)

    private var hikariDataSource: HikariDataSource? = null


    // NOTE: to allow access mysql using 'root' (and EMPTY password) user you have to change auth method. Please
    // visit link for more information: https://stackoverflow.com/questions/41645309/mysql-error-access-denied-for-user-rootlocalhost
    fun getHikariInstance(): HikariDataSource {
        if (hikariDataSource == null) {
            val databaseUrl: String = getDatabaseUrl()
            LOG.info("setup database.  DatabaseUrl: $databaseUrl")
            LOG.debug("===================== setup database url: $databaseUrl")
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = databaseUrl
                username = CONF_DB_USERNAME
                password = CONF_DB_PASS
                minimumIdle = 10
                maximumPoolSize = 30
                connectionTimeout = 5_000   // This property controls the maximum number of milliseconds that a client (that's you) will wait for a connection from the pool.
                addDataSourceProperty("cachePrepStmts", "true")
                addDataSourceProperty("prepStmtCacheSize", "250")
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
//                leakDetectionThreshold = 3000  // Hikari will log query if it takes more the 3s in this case
            }

//            enableLogging()

            hikariDataSource = HikariDataSource(hikariConfig)
        }
        return hikariDataSource!!
    }

    private fun enableLogging() {
        val hikariLogger = LoggerFactory.getLogger("com.zaxxer.hikari") as Logger
        hikariLogger.level = ch.qos.logback.classic.Level.TRACE
    }

    private fun getDatabaseUrl(): String {
        return "jdbc:mysql://localhost:$CONF_DB_PORT/dynamic_url?useUnicode=true&characterEncoding=utf8&useSSL=false"
    }
}
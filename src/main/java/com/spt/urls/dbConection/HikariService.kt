package com.spt.urls.dbConection

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.spt.urls.logs.TicketLoggerFactory
import com.spt.urls.webService.CONF_DB_PASS
import com.spt.urls.webService.CONF_DB_USERNAME

class HikariService {
    private val LOG = TicketLoggerFactory.getTicketLogger(HikariService::class.java)

    private var hikariDataSource: HikariDataSource? = null


    // NOTE: to allow access mysql using 'root' (and EMPTY password) user you have to change auth method. Please
    // visit link for more information: https://stackoverflow.com/questions/41645309/mysql-error-access-denied-for-user-rootlocalhost
    fun getHikariInstance(): HikariDataSource {
        if (hikariDataSource == null) {
            val databaseUrl: String = getDatabaseUrl()
            println("****************** setup database url: $databaseUrl")
            LOG.debug("===================== setup database url: $databaseUrl")
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = databaseUrl
                username = CONF_DB_USERNAME
                password = CONF_DB_PASS
                maximumPoolSize = 30
                connectionTimeout = 5_000   // This property controls the maximum number of milliseconds that a client (that's you) will wait for a connection from the pool.
                addDataSourceProperty("cachePrepStmts", "true")
                addDataSourceProperty("prepStmtCacheSize", "250")
                addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            }

            hikariDataSource = HikariDataSource(hikariConfig)
        }
        return hikariDataSource!!
    }

    private fun getDatabaseUrl(): String {
        return "jdbc:mysql://localhost:3306/dynamic_url?useUnicode=true&characterEncoding=utf8&useSSL=false"
    }
}
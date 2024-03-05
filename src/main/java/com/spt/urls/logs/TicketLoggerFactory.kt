package com.spt.urls.logs

import org.slf4j.LoggerFactory

object TicketLoggerFactory {

    fun getTicketLogger(clazz: Class<*>?): TicketLogger {
        val logger = LoggerFactory.getLogger(clazz)
        return TicketLogger(logger)
    }
}
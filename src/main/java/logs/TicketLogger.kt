package logs

import org.slf4j.Logger

class TicketLogger(private val logger: Logger) {

    fun info(msg: String?) {
        logger.info(msg)
    }

    fun debug(msg: String?, th: Throwable? = null) {
        logger.debug(msg, th)
    }

    fun error(msg: String?, th: Throwable? = null) {
        logger.error(msg, th)
    }
}
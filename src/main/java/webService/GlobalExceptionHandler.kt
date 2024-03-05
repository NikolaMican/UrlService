package webService

import logs.TicketLoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.HttpRequestMethodNotSupportedException


@ControllerAdvice
class GlobalExceptionHandler {

    private val LOG = TicketLoggerFactory.getTicketLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<Any> {
        if (ex is ResponseStatusException) {
            LOG.error(ex.message)
            return ResponseEntity(ex.status)
        }
        if (ex is HttpRequestMethodNotSupportedException) {
            // this was case:   org.springframework.web.HttpRequestMethodNotSupportedException: Request method 'POST' not supported
            LOG.error(ex.message)
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }

        LOG.error(ex.message, ex)
        val body: MutableMap<String, Any> = HashMap<String, Any>().apply {
            this["message"] = "An error occurred"
        }
        return ResponseEntity(body, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
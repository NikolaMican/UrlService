package com.spt.urls.webService.protocolVersion1

import com.spt.urls.logs.TicketLoggerFactory
import org.springframework.boot.logging.LogLevel
import org.springframework.boot.logging.LoggingSystem
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException


data class LogLevelUrlRequest(val logLevel: String?, val loggerName: String?)

@RestController
class LogLevelWebServiceRestController {
    private val LOG = TicketLoggerFactory.getTicketLogger(LogLevelWebServiceRestController::class.java)

    @PostMapping("/v1/logLevel")
    @ResponseBody
    fun logLevelPost(
        @RequestBody request: LogLevelUrlRequest,
    ) {
        LOG.info("Processing logLevel request. Please don't forget to pass full qualified class name. eg. com.spt.urls.webService.ClickOnDynamicUrlWebServiceController")
        if (request.logLevel.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "logLevel is null.")
        if (request.loggerName.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "loggerName is null.")
        LOG.debug("Received params, logLevel: ${request.logLevel}, loggerName: ${request.loggerName}")

        try {
            val system = LoggingSystem.get(LogLevelWebServiceRestController::class.java.classLoader)
            system.setLogLevel(request.loggerName, LogLevel.valueOf(request.logLevel))
        } catch (e: Exception) {
            LOG.error("Failed to update log level", e)
            throw ResponseStatusException(BAD_REQUEST, "Failed to update log level")
        }
        LOG.debug("Processing logLevel request finished.")
    }
}


@Controller
class LogLevelWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(LogLevelWebServiceController::class.java)

    private val restController = LogLevelWebServiceRestController()

    @GetMapping("/v1/logLevelGet")
    @ResponseBody
    fun logLevelGet(
        @RequestParam(name = "logLevel") logLevel: String?,
        @RequestParam(required = false, name = "loggerName") loggerName: String?
    ) {
        return restController.logLevelPost(LogLevelUrlRequest(logLevel, loggerName))
    }
}
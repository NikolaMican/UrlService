package com.spt.urls.webService

import com.spt.urls.di.di
import com.spt.urls.extensions.getNormalisedUrl
import com.spt.urls.extensions.throwExceptionIfRedirectUrlContainsOurDomain
import com.spt.urls.logs.TicketLoggerFactory
import com.spt.urls.webService.beans.CreateDynamicUrlResponse
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import java.sql.SQLException
import java.util.*


@Controller
class CreateDynamicUrlWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(CreateDynamicUrlWebServiceController::class.java)

    private val dynamicUrlService = di().getDynamicUrlService()
    private val userDbController = di().getUserDbController()

    @GetMapping("/createDynamicUrl") // oznacava da li ce uci u metodu
    @ResponseBody
    @Throws(SQLException::class)
    fun createDynamicUrl(
        @RequestParam(name = "apiKey") apiKey: String?,
        @RequestParam(name = "redirectUrl") redirectUrl: String?,
        @RequestParam(required = false, name = "clientCustomPath") clientCustomPath: String?
    ): CreateDynamicUrlResponse {
        // request example:   http://localhost:8080/createDynamicUrl?redirectUrl=www.rts.rs
        LOG.info("Processing createDynamicUrl request. Received redirectUrl: $redirectUrl")
        if (redirectUrl == null) throw ResponseStatusException(BAD_REQUEST, "Received redirectUrl is null.")
        if (apiKey == null) throw ResponseStatusException(BAD_REQUEST, "Received apiKey is null.")
        val user = userDbController.getByApiKey(apiKey) ?: throw ResponseStatusException(BAD_REQUEST, "Api key doesn't exist in database.")

        val normalisedRedirectUrl = redirectUrl.lowercase().getNormalisedUrl()
        normalisedRedirectUrl.throwExceptionIfRedirectUrlContainsOurDomain()

        val url = dynamicUrlService.createDynamicUrl(user = user, redirectUrl= normalisedRedirectUrl, clientCustomPath= clientCustomPath ?: "")
        return CreateDynamicUrlResponse(url)
    }


    @GetMapping("/redirect2")
    @ResponseBody
    fun redirect2(): ModelAndView {
        LOG.info("Processing redirect2 request")
        return ModelAndView("redirect:$CONF_HTTP_PROTOCOL://www.yahoo.com")
    }

}
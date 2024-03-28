package com.spt.urls.webService

import com.spt.urls.di.di
import com.spt.urls.extensions.getNormalisedUrl
import com.spt.urls.extensions.throwExceptionIfRedirectUrlContainsOurDomain
import com.spt.urls.logs.TicketLoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView

data class CreateDynamicUrlRequest(val apiKey: String?, val redirectUrl: String?, val clientCustomPath: String?)
data class CreateDynamicUrlResponse(var url: String)

@RestController
class CreateDynamicUrlWebServiceRestController {
    private val LOG = TicketLoggerFactory.getTicketLogger(CreateDynamicUrlWebServiceRestController::class.java)

    private val dynamicUrlService = di().getDynamicUrlService()
    private val userDbController = di().getUserDbController()

    @PostMapping("/createDynamicUrl")
    @ResponseBody
    fun createDynamicUrlPost(
        @RequestBody request: CreateDynamicUrlRequest,
    ): CreateDynamicUrlResponse {
        LOG.info("Processing createDynamicUrl request. Received redirectUrl: ${request.redirectUrl}")
        if (request.redirectUrl == null) throw ResponseStatusException(BAD_REQUEST, "Received redirectUrl is null.")
        if (request.apiKey == null) throw ResponseStatusException(BAD_REQUEST, "Received apiKey is null.")
        val user = userDbController.getByApiKey(request.apiKey) ?: throw ResponseStatusException(BAD_REQUEST, "Api key doesn't exist in database.")

        val normalisedRedirectUrl = request.redirectUrl.lowercase().getNormalisedUrl()
        normalisedRedirectUrl.throwExceptionIfRedirectUrlContainsOurDomain()

        val url = dynamicUrlService.createDynamicUrl(user = user, redirectUrl= normalisedRedirectUrl, clientCustomPath= request.clientCustomPath ?: "")
        return CreateDynamicUrlResponse(url)
    }
}


@Controller
class CreateDynamicUrlWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(CreateDynamicUrlWebServiceController::class.java)

    private val restController = CreateDynamicUrlWebServiceRestController()

    @GetMapping("/createDynamicUrlGet") // oznacava da li ce uci u metodu
    @ResponseBody
    fun createDynamicUrlGet(
        @RequestParam(name = "apiKey") apiKey: String?,
        @RequestParam(name = "redirectUrl") redirectUrl: String?,
        @RequestParam(required = false, name = "clientCustomPath") clientCustomPath: String?
    ): CreateDynamicUrlResponse {
        return restController.createDynamicUrlPost(CreateDynamicUrlRequest(apiKey, redirectUrl, clientCustomPath))
    }


    @GetMapping("/redirect2")
    @ResponseBody
    fun redirect2(): ModelAndView {
        LOG.info("Processing redirect2 request")
        return ModelAndView("redirect:$CONF_HTTP_PROTOCOL://www.yahoo.com")
    }
}
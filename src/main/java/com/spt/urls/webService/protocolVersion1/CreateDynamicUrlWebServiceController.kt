package com.spt.urls.webService.protocolVersion1

import com.spt.urls.CONF_HTTP_PROTOCOL
import com.spt.urls.di.di
import com.spt.urls.dynamicUrl.DynamicUrlBean
import com.spt.urls.extensions.getNormalisedUrl
import com.spt.urls.extensions.throwExceptionIfRedirectUrlContainsOurDomain
import com.spt.urls.logs.TicketLoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView

data class CreateDynamicUrlRequest(val apiKey: String?, val alias: String?, val destinationUrl: String?, val clientCustomPath: String?)
data class CreateDynamicUrlResponse(val urlId: String, val alias: String?, val shortUrl: String)

@RestController
class CreateDynamicUrlWebServiceRestController {
    private val LOG = TicketLoggerFactory.getTicketLogger(CreateDynamicUrlWebServiceRestController::class.java)

    private val dynamicUrlService = di().getDynamicUrlService()
    private val userDbController = di().getUserDbController()

    @PostMapping("/v1/createDynamicUrl")
    @ResponseBody
    fun createDynamicUrlPost(
        @RequestBody request: CreateDynamicUrlRequest,
    ): CreateDynamicUrlResponse {
        LOG.info("Processing createDynamicUrl request. Received destinationUrl: ${request.destinationUrl}")
        if (request.destinationUrl.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received destinationUrl is null.")
        if (request.apiKey.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received apiKey is null.")
        if (request.alias.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received alias is null.")

        val user = userDbController.getByApiKey(request.apiKey) ?: throw ResponseStatusException(BAD_REQUEST, "Api key doesn't exist in database.")

        val normalisedRedirectUrl = request.destinationUrl.lowercase().getNormalisedUrl()
        normalisedRedirectUrl.throwExceptionIfRedirectUrlContainsOurDomain()

        val dynamicUrl = dynamicUrlService.createDynamicUrl(user = user, alias = request.alias, redirectUrl= normalisedRedirectUrl, clientCustomPath= request.clientCustomPath ?: "")
        return CreateDynamicUrlResponse(urlId = dynamicUrl.urlId, alias = request.alias, shortUrl = dynamicUrl.shortUrl)
    }
}


@Controller
class CreateDynamicUrlWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(CreateDynamicUrlWebServiceController::class.java)

    private val restController = CreateDynamicUrlWebServiceRestController()

    @GetMapping("/v1/createDynamicUrlGet") // oznacava da li ce uci u metodu
    @ResponseBody
    fun createDynamicUrlGet(
        @RequestParam(name = "apiKey") apiKey: String?,
        @RequestParam(name = "alias") alias: String?,
        @RequestParam(name = "destinationUrl") destinationUrl: String?,
        @RequestParam(required = false, name = "clientCustomPath") clientCustomPath: String?
    ): CreateDynamicUrlResponse {
        return restController.createDynamicUrlPost(CreateDynamicUrlRequest(apiKey, alias, destinationUrl, clientCustomPath))
    }


    @GetMapping("/redirect2")
    @ResponseBody
    fun redirect2(): ModelAndView {
        LOG.info("Processing redirect2 request")
        return ModelAndView("redirect:$CONF_HTTP_PROTOCOL://www.yahoo.com")
    }
}
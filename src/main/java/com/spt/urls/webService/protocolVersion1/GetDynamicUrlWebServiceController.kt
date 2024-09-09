package com.spt.urls.webService.protocolVersion1

import com.spt.urls.CONF_DOMAIN
import com.spt.urls.di.di
import com.spt.urls.dynamicUrl.DynamicUrlBean
import com.spt.urls.extensions.getUrlWithDomain
import com.spt.urls.logs.TicketLoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus.BAD_REQUEST


data class GetDynamicUrlRequest(val apiKey: String?=null)
data class DynamicUrlResponse(val alias: String,//"test-yahoo",
                              val urlId: String, //"Dn1bkkLi",
                              val shortUrl: String, //"<DOMAIN>/?Dn1bkkLi",
                                val destinationUrl: String, //"http://yahoo.com",
                                val numOfClicks: Int)
data class GetDynamicUrlResponse(val urls: List<DynamicUrlResponse>)

@RestController
open class GetDynamicUrlWebServiceRestController {
    private val LOG = TicketLoggerFactory.getTicketLogger(GetDynamicUrlWebServiceRestController::class.java)

    private val dynamicUrlService = di().getDynamicUrlService()
    private val userDbController = di().getUserDbController()

    @PostMapping("/v1/getDynamicUrls")
    @ResponseBody
    fun getDynamicUrlsPost(
        @RequestBody request: GetDynamicUrlRequest,
    ): GetDynamicUrlResponse {
        LOG.info("Processing getDynamicUrls request.")
        if (request.apiKey.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received apiKey is null.")

        val user = userDbController.getByApiKey(request.apiKey) ?: throw ResponseStatusException(BAD_REQUEST, "Api key doesn't exist in database.")

        val urls = dynamicUrlService.getDynamicUrls(user = user).map {
            DynamicUrlResponse(alias = it.alias, urlId = it.urlId,
                                destinationUrl = it.destinationUrl, numOfClicks = it.numOfClicks,
                                shortUrl = it.dynamicUrlTemplate.getUrlWithDomain()

            )
        }
        return GetDynamicUrlResponse(urls)
    }
}


@Controller
class GetDynamicUrlWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(GetDynamicUrlWebServiceController::class.java)

    private val restController = GetDynamicUrlWebServiceRestController()

    @GetMapping("/v1/getDynamicUrlsGet")
    @ResponseBody
    fun getDynamicUrlsGet(
        @RequestParam(name = "apiKey") apiKey: String?,
    ): GetDynamicUrlResponse {
        return restController.getDynamicUrlsPost(GetDynamicUrlRequest(apiKey))
    }
}
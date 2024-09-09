package com.spt.urls.webService.protocolVersion1

import com.spt.urls.di.di
import com.spt.urls.extensions.getNormalisedUrl
import com.spt.urls.extensions.throwExceptionIfRedirectUrlContainsOurDomain
import com.spt.urls.logs.TicketLoggerFactory
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.SQLException
import java.util.*

data class EditDynamicUrlRequest(val apiKey: String?, val alias: String?, val destinationUrl: String?, val urlId: String?)

@RestController
class EditDynamicUrlWebServiceRestController {
    private val LOG = TicketLoggerFactory.getTicketLogger(EditDynamicUrlWebServiceRestController::class.java)

    private val dynamicUrlDbController = di().getDynamicUrlDbController()
    private val userDbController = di().getUserDbController()

    @PostMapping("/v1/editDynamicUrl")
    @ResponseBody
    fun editDynamicUrlPost(
        @RequestBody request: EditDynamicUrlRequest,
    ) {
        LOG.info("Processing editDynamicUrlPost request. Received redirectUrl: ${request.destinationUrl}, urlId: ${request.urlId}")
        if (request.destinationUrl.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received redirectUrl is null.")
        if (request.alias.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received alias is null.")
        if (request.apiKey.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received apiKey is null.")
        if (request.urlId.isNullOrBlank()) throw ResponseStatusException(BAD_REQUEST, "Received urlId is null.")

        val user = userDbController.getByApiKey(request.apiKey) ?: throw ResponseStatusException(BAD_REQUEST, "Api key doesn't exist in database.")

        val dUBean = dynamicUrlDbController.get(user.idUser, request.urlId) ?: throw ResponseStatusException(BAD_REQUEST, "Required urlId: '${request.urlId}' doesn't exits for user: ${user.username}")
        dUBean.destinationUrl = request.destinationUrl.lowercase().getNormalisedUrl()
        dUBean.destinationUrl.throwExceptionIfRedirectUrlContainsOurDomain()
        dUBean.alias = request.alias
        dynamicUrlDbController.edit(dUBean)

        //   http://localhost:8080/editDynamicUrl?urlId=123456&redirectUrl=www.rts.rs
        //   http://localhost:80808/editDynamicUrl?urlId=12345&redirectUrl=https://www.rts.rs
//        val map: HashMap<String, String> = HashMap()
//        map["urlId"] = "12345"
//        map["redirectUrl"] = "www.rts.rs"
    }
}



@Controller
class EditDynamicUrlWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(EditDynamicUrlWebServiceController::class.java)

    private val restController = EditDynamicUrlWebServiceRestController()

    @GetMapping("/v1/editDynamicUrlGet")
    @ResponseBody
    @Throws(SQLException::class)
    fun editDynamicUrl(
        @RequestParam(name = "apiKey") apiKey: String?,
        @RequestParam(name = "alias") alias: String,
        @RequestParam(name = "urlId") urlId: String,
        @RequestParam(name = "destinationUrl") destinationUrl: String
    ) {
        restController.editDynamicUrlPost(EditDynamicUrlRequest(
            apiKey = apiKey,
            alias = alias,
            destinationUrl = destinationUrl,
            urlId = urlId
        ))
    }
}
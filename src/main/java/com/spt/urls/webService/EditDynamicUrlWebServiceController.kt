package com.spt.urls.webService

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


@Controller
class EditDynamicUrlWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(EditDynamicUrlWebServiceController::class.java)

    private val dynamicUrlDbController = di().getDynamicUrlDbController()
    private val userDbController = di().getUserDbController()

    @GetMapping("/editDynamicUrl")
    @ResponseBody
    @Throws(SQLException::class)
    fun editDynamicUrl(
        @RequestParam(name = "apiKey") apiKey: String?,
        @RequestParam(name = "urlId") urlId: String,
        @RequestParam(name = "redirectUrl") redirectUrl: String
    ) {
        if (apiKey == null) throw ResponseStatusException(BAD_REQUEST, "Received apiKey is null.")
        val user = userDbController.getByApiKey(apiKey) ?: throw ResponseStatusException(BAD_REQUEST, "Api key doesn't exist in database.")

        LOG.info("Processing editDynamicUrl request, urlId: $urlId, redirectUrl: $redirectUrl")
        val dUBean = dynamicUrlDbController.get(user.idUser, urlId) ?: throw ResponseStatusException(BAD_REQUEST, "Required urlId: '$urlId' doesn't exits for user: ${user.username}")
        dUBean.redirectUrl = redirectUrl.lowercase().getNormalisedUrl()
        dUBean.redirectUrl.throwExceptionIfRedirectUrlContainsOurDomain()
        dynamicUrlDbController.edit(dUBean)

        //   http://localhost:8080/editDynamicUrl?urlId=123456&redirectUrl=www.rts.rs
        //   http://localhost:80808/editDynamicUrl?urlId=12345&redirectUrl=https://www.rts.rs
//        val map: HashMap<String, String> = HashMap()
//        map["urlId"] = "12345"
//        map["redirectUrl"] = "www.rts.rs"
    }
}
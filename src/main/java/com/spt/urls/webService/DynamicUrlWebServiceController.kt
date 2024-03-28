/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spt.urls.webService

import com.spt.urls.di.di
import com.spt.urls.logs.TicketLoggerFactory
import com.spt.urls.services.LocationApiResponse
import com.spt.urls.webService.beans.CreateDynamicUrlResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.ModelAndView
import java.sql.SQLException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
class DynamicUrlWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(DynamicUrlWebServiceController::class.java)

    private val dynamicUrlService = di().getDynamicUrlService()
    private val dynamicUrlDbController = di().getDynamicUrlDbController()
    private val userDbController = di().getUserDbController()
    private val headerService = di().getHeaderService()
    private val locationService = di().getLocationService()

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
        if (redirectUrl == null) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Received redirectUrl is null.")
        if (apiKey == null) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Received apiKey is null.")
        val user = userDbController.getByApiKey(apiKey) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Api key doesn't exist in database.")

        val normalisedRedirectUrl = getNormalisedUrl(redirectUrl.lowercase())
        throwExceptionIfRedirectUrlContainsOurDomain(normalisedRedirectUrl)

        val url = dynamicUrlService.createDynamicUrl(user = user, redirectUrl= normalisedRedirectUrl, clientCustomPath= clientCustomPath ?: "")
        return CreateDynamicUrlResponse(url)
    }

    private fun getNormalisedUrl(url: String): String {
        if (url.startsWith("https://") || url.startsWith("http://")) {
            return url
        }
        return "http://$url"
    }

    @GetMapping("/editDynamicUrl")
    @ResponseBody
    @Throws(SQLException::class)
    fun editDynamicUrl(
        @RequestParam(name = "apiKey") apiKey: String?,
        @RequestParam(name = "urlId") urlId: String,
        @RequestParam(name = "redirectUrl") redirectUrl: String
    ) {
        if (apiKey == null) throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Received apiKey is null.")
        val user = userDbController.getByApiKey(apiKey) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Api key doesn't exist in database.")

        LOG.info("Processing editDynamicUrl request, urlId: $urlId, redirectUrl: $redirectUrl")
        val dUBean = dynamicUrlDbController.get(user.idUser, urlId) ?: throw ResponseStatusException(HttpStatus.NOT_EXTENDED, "Required urlId: '$urlId' doesn't exits for user: ${user.username}")
        dUBean.redirectUrl = getNormalisedUrl(redirectUrl.lowercase())
        throwExceptionIfRedirectUrlContainsOurDomain(dUBean.redirectUrl)
        dynamicUrlDbController.edit(dUBean)

        //   http://localhost:8080/editDynamicUrl?urlId=123456&redirectUrl=www.rts.rs
        //   http://localhost:80808/editDynamicUrl?urlId=12345&redirectUrl=https://www.rts.rs
//        val map: HashMap<String, String> = HashMap()
//        map["urlId"] = "12345"
//        map["redirectUrl"] = "www.rts.rs"
    }

    @RequestMapping(value = ["/"], method = [RequestMethod.GET])
    @Throws(SQLException::class)
    fun clickOnDynamicUrl(request: HttpServletRequest, httpServletResponse: HttpServletResponse) {
        clickOnDynamicUrlImpl(request, httpServletResponse, null)
    }

    @RequestMapping(value = ["/{clientCustomPath}"], method = [RequestMethod.GET])
    @Throws(SQLException::class)
    fun clickOnDynamicUrlClientCustomPath(
        @PathVariable(name = "clientCustomPath")  clientCustomPath: String,
        request: HttpServletRequest, httpServletResponse: HttpServletResponse
    ) {
        LOG.info("[clickOnDynamicUrlClientCustomPath] clientCustomPath: $clientCustomPath")
        clickOnDynamicUrlImpl(request, httpServletResponse, clientCustomPath)
    }

    private fun clickOnDynamicUrlImpl(request: HttpServletRequest,
                                      httpServletResponse: HttpServletResponse,
                                      clientCustomPath: String?
    ) {
        printHeader(request)

        val clientIp = request.getHeader("x-forwarded-for") ?: request.remoteAddr
        LOG.info("\t clientIp: $clientIp")
        val location =  getLocation(clientIp)

        val county = location?.country
        val city = location?.city
        LOG.info("county: $county, city: $city")

        //   http://localhost:8080/?123456
        val fullUrl = headerService.getFullURL(request)
        LOG.info("process click on url request. Url: $fullUrl")
        val browserName = headerService.getBrowserName(
//            request.getHeader("sec-ch-ua"),
            request.getHeader("user-agent")
        )
        val platformName = headerService.getPlatformName(
            request.getHeader("sec-ch-ua-platform"),
            request.getHeader("user-agent")
        )
        val isMobilePlatform = headerService.isMobile(request.getHeader("user-agent"))

        var redirectUrl = dynamicUrlService.clickOnDynamicUrl(fullUrl, browserName, platformName, isMobilePlatform, county, clientCustomPath)
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "There is no redirect url in database for requested url: $fullUrl")
        redirectUrl = getNormalisedUrl(redirectUrl.lowercase())
        throwExceptionIfRedirectUrlContainsOurDomain(redirectUrl)

        LOG.info("redirect to: $redirectUrl")
        httpServletResponse.setHeader("Location", redirectUrl)
        httpServletResponse.status = 302
    }

    private fun getLocation(clientIpAddress: String?): LocationApiResponse? {
        return null   // @TODO this is temporary solution

//        val location =
//            try {
//                LOG.info("\t clientIp: $clientIpAddress")
//                if (clientIpAddress == null) {
//                    null
//                } else {
//                    locationService.getLocationUseApacheHttpClient(clientIpAddress)
//                }
//        } catch (e: Exception) {
//            LOG.error("failed to get location", e)
//            null
//        }
//        return location
    }

    private fun throwExceptionIfRedirectUrlContainsOurDomain(redirectUrl: String) {
        if (redirectUrl.contains(MY_IP_ADDRESS) || redirectUrl.contains(CONF_DOMAIN)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "It\'s not allowed to use our domain links in redirectUrl: $redirectUrl. We prevent infinity loop with it.")
        }
    }


    private fun printHeader(request: HttpServletRequest) {
        LOG.debug("header params - debug")
        LOG.info("header params")
        val header = request.headerNames
        while (header.hasMoreElements()) {
            val attributeName = header.nextElement()
            LOG.info("\t attributeName: "+ attributeName + ", value: " + request.getHeader(attributeName))
        }
    }

    @GetMapping("/redirect2")
    @ResponseBody
    fun redirect2(): ModelAndView {
        LOG.info("Processing redirect2 request")
        return ModelAndView("redirect:$CONF_HTTP_PROTOCOL://www.yahoo.com")
    }

}
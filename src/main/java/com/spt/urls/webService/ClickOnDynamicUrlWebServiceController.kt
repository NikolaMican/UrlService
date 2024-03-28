package com.spt.urls.webService

import com.spt.urls.di.di
import com.spt.urls.extensions.getNormalisedUrl
import com.spt.urls.extensions.throwExceptionIfRedirectUrlContainsOurDomain
import com.spt.urls.logs.TicketLoggerFactory
import com.spt.urls.services.LocationApiResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.sql.SQLException
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Controller
class ClickOnDynamicUrlWebServiceController {
    private val LOG = TicketLoggerFactory.getTicketLogger(ClickOnDynamicUrlWebServiceController::class.java)

    private val dynamicUrlService = di().getDynamicUrlService()
    private val headerService = di().getHeaderService()


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
        redirectUrl = redirectUrl.lowercase().getNormalisedUrl()
        redirectUrl.throwExceptionIfRedirectUrlContainsOurDomain()

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


    private fun printHeader(request: HttpServletRequest) {
        LOG.debug("header params - debug")
        LOG.info("header params")
        val header = request.headerNames
        while (header.hasMoreElements()) {
            val attributeName = header.nextElement()
            LOG.info("\t attributeName: "+ attributeName + ", value: " + request.getHeader(attributeName))
        }
    }
}
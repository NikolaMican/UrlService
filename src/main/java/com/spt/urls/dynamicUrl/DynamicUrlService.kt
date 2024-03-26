package com.spt.urls.dynamicUrl

import com.spt.urls.dynamicUrlDetails.DynamicUrlDetailsBean
import com.spt.urls.dynamicUrlDetails.DynamicUrlDetailsDbController
import com.spt.urls.logs.TicketLoggerFactory
import com.spt.urls.services.RandomService
import com.spt.urls.webService.CONF_DOMAIN
import com.spt.urls.webService.CONF_HTTP_PROTOCOL
import com.spt.urls.webService.CONF_PORT

/**
 *
 * @author Nikola Micanovic
 */
class DynamicUrlService(
    val dynamicUrlDBController: DynamicUrlDbController,
    val dynamicUrlDetailsDBController: DynamicUrlDetailsDbController,
    val randomService: RandomService
) {
    private val LOG = TicketLoggerFactory.getTicketLogger(DynamicUrlService::class.java)

    private val serverUseDefaultPort by lazy {
           CONF_HTTP_PROTOCOL == "http" && CONF_PORT.toString() == "80"
        || CONF_HTTP_PROTOCOL == "https" && CONF_PORT.toString()  == "443"
    }

    fun clickOnDynamicUrl(url: String, browser: String?, platform: String?, isMobilePlatform: Boolean, location: String?): String? {
        val urlId = getUrlId(url)

        if (urlId == null) {
            LOG.error("urlId is null.")
            return null
        }
        val dynamicUrlBean = dynamicUrlDBController.get(urlId)
        if (dynamicUrlBean == null) {
            LOG.error("urlId: $urlId doesn't exist in database")
            return null
        }

        dynamicUrlBean.numOfClicks += 1
        dynamicUrlDBController.edit(dynamicUrlBean)

        // every time when new click happend, insert new line to 'DynamicUrlDetails' table
        val dynamicUrlId = dynamicUrlBean.idDynamicUrl
        val dudBean = DynamicUrlDetailsBean(dynamicUrlId, System.currentTimeMillis(), location, browser, platform, isMobilePlatform)
        dynamicUrlDetailsDBController.insert(dudBean)
        return dynamicUrlBean.redirectUrl
    }

    private fun getUrlId(url: String): String? {
        val urlIdPrefix = '?'
        val lastPrefixIndex = url.lastIndexOf(urlIdPrefix)
        return if (lastPrefixIndex == -1) null else url.substring(lastPrefixIndex + 1)
    }

    fun createDynamicUrl(redirectUrl: String?): String {
        val urlId = randomService.randomString(6)

        // if http protocol is http  => localhost/?21356
        // if http protocol is https => https://localhost/?21356

        var url = CONF_DOMAIN
        if (CONF_HTTP_PROTOCOL == "https") {
            url = "$CONF_HTTP_PROTOCOL://$url"
        }
        url += if (serverUseDefaultPort) "" else ":$CONF_PORT"
        url += "/?$urlId"

        val dynamicUrlBean = DynamicUrlBean(urlId, redirectUrl!!, 0)
        dynamicUrlDBController.insert(dynamicUrlBean)

        // http://localhost:8080/?21356
        return url
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynamicUrl

import dynamicUrlDetails.DynamicUrlDetailsBean
import dynamicUrlDetails.DynamicUrlDetailsDbController
import logs.TicketLoggerFactory
import services.RandomService
import webService.CONF_DOMAIN
import webService.CONF_HTTP_PROTOCOL
import webService.CONF_PORT

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

        var url = "$CONF_HTTP_PROTOCOL://$CONF_DOMAIN"
        url += if (serverUseDefaultPort) "" else ":$CONF_PORT"
        url += "/?$urlId"

        val dynamicUrlBean = DynamicUrlBean(urlId, redirectUrl!!, 0)
        dynamicUrlDBController.insert(dynamicUrlBean)

        // http://localhost:8080/?21356
        return url
    }
}
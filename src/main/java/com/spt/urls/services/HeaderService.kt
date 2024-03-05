package com.spt.urls.services

import java.util.*
import javax.servlet.http.HttpServletRequest

class HeaderService {

    private val PLATFORM_ANDROID = "android"
    private val PLATFORM_IOS = "ios"
    private val PLATFORM_MAC_OS = "mac os"
    private val PLATFORM_WINDOWS = "windows"
    private val PLATFORM_LINUX = "linux"
    private val SUPPORTED_PLATFORMS = listOf(
        Pair("android", PLATFORM_ANDROID),
        Pair("iphone", PLATFORM_IOS),
        Pair("ipad", PLATFORM_IOS),
        Pair("mac os", PLATFORM_MAC_OS),
        Pair("windows", PLATFORM_WINDOWS),
        Pair("linux", PLATFORM_LINUX)
    )

    // order is important. Add more specific at the beginning
    private val BROWSER_CHROMIUM = "chromium"
    private val BROWSER_OPERA = "opera"
    private val BROWSER_EDGE = "edge"
    private val BROWSER_CHROME = "chrome"
    private val BROWSER_SAFARI = "safari"
    private val BROWSER_MOZILLA = "mozilla"
    private val SUPPORTED_BROWSERS = listOf(
        Pair("chromium", BROWSER_CHROMIUM),
        Pair("opr", BROWSER_OPERA),     // desktop opera
        Pair("opt", BROWSER_OPERA),     // mobile opera
        Pair("edg", BROWSER_EDGE),
        Pair("chrome", BROWSER_CHROME),
        Pair("safari", BROWSER_SAFARI),
        Pair("mozilla", BROWSER_MOZILLA)
    )

//        arrayOf( "chromium", "opr", "chrome", "safari", "mozilla") // order is important. Add more specific at the beginning

    fun getFullURL(req: HttpServletRequest): String {
        //     http://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123&d=789
        //     https://hostname.com:80/mywebapp/servlet/MyServlet/a/b;c=123&d=789
        //      http://localhost:8080/?mywebapp/servlet/MyServlet/a/b;c=123&d=789
        val scheme = req.scheme // http
        val serverName = req.serverName // hostname.com
        val serverPort = req.serverPort // 80
        val contextPath = req.contextPath // /mywebapp
        val servletPath = req.servletPath // /servlet/MyServlet
        val pathInfo = req.pathInfo // /a/b;c=123
        val queryString = req.queryString // d=789

        // Reconstruct original requesting URL
        val url = StringBuilder()
        url.append(scheme).append("://").append(serverName)
        if (serverPort != 80 && serverPort != 443) {
            url.append(":").append(serverPort)
        }
        url.append(contextPath).append(servletPath)
        pathInfo?.let { url.append(it) }
        queryString?.let { url.append("?").append(it) }

        return url.toString()
    }

    fun getPlatformName(vararg platformUrlText: String?): String? {
        for (urlText in platformUrlText) {
            if (urlText == null) {
                continue
            }
            val lowercasePlatformUrlText = urlText.lowercase(Locale.getDefault())

            for ((platformHeaderName, dbHeaderName) in SUPPORTED_PLATFORMS) {
                if (lowercasePlatformUrlText.contains(platformHeaderName)) return dbHeaderName
            }
        }
        return null
    }

    fun getBrowserName(vararg browserUrlText: String?): String? {
        for (urlText in browserUrlText) {
            if (urlText == null) {
                continue
            }
            val lowercaseBrowserUrlText = urlText.lowercase(Locale.getDefault())
            for ((browserNameInHeader, dbBrowserName) in SUPPORTED_BROWSERS) {
                if (lowercaseBrowserUrlText.contains(browserNameInHeader)) return dbBrowserName
            }
        }
        return null
    }

    fun isMobile(userAgent: String?): Boolean {
        return userAgent?.lowercase()?.contains("mobile") ?: false
    }
}
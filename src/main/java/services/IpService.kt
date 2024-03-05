package services

import webService.CONF_HTTP_PROTOCOL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class IpService {
    fun getMyIpAddress(): String {
        val ipAdress: URL
        try {
            ipAdress = URL("$CONF_HTTP_PROTOCOL://myexternalip.com/raw")
            ipAdress.openStream().use {
                val reader = BufferedReader(InputStreamReader(it))
                val ip = reader.readLine()
                return ip
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return "unknownIpAddress"
        }
    }
}
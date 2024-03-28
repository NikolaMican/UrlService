package com.spt.urls

import com.spt.urls.services.LocationApiResponse
import java.util.concurrent.ConcurrentHashMap

class LocationCache {
    private val map = ConcurrentHashMap<String, LocationApiResponse>(2100)

    private fun getMap(): ConcurrentHashMap<String, LocationApiResponse> {
        if (map.size < 2000) {
            return map
        }
        map.clear()
        return map
    }

    fun getLocation(clientIpAddress: String): LocationApiResponse? {
        return getMap()[clientIpAddress]
    }

    fun setLocation(clientIpAddress: String, locationApiResponse: LocationApiResponse) {
        getMap()[clientIpAddress] = locationApiResponse
    }
}
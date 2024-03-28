package com.spt.urls.di

import com.spt.urls.LocationCache
import com.spt.urls.db.HikariService
import com.spt.urls.db.user.UserDbController
import com.spt.urls.dynamicUrl.DynamicUrlDbController
import com.spt.urls.dynamicUrl.DynamicUrlService
import com.spt.urls.dynamicUrlDetails.DynamicUrlDetailsDbController
import com.spt.urls.services.HeaderService
import com.spt.urls.services.LocationService
import com.spt.urls.services.RandomService

fun di() = DiManager.instance

class DiManager private constructor(){
    companion object {
        val instance: DiManager by lazy {
            println("===== [DiManager] getInstance lazy")
            DiManager()
        }
    }

    private val randomService = RandomService()
    private val headerService = HeaderService()
    private val locationCache = LocationCache()
    private val locationService = LocationService(locationCache)
    private val hikariService = HikariService()
    private val userDbController = UserDbController(hikariService)
    private val dynamicUrlDbController = DynamicUrlDbController(hikariService)
    private val dynamicUrlDetailsDBController = DynamicUrlDetailsDbController(hikariService)
    private val dynamicUrlService = DynamicUrlService(dynamicUrlDbController, dynamicUrlDetailsDBController, userDbController, randomService)



    fun getRandomService(): RandomService = randomService
    fun getHeaderService(): HeaderService = headerService
    fun getLocationService(): LocationService = locationService
    fun getHikariService(): HikariService = hikariService
    fun getDynamicUrlService(): DynamicUrlService = dynamicUrlService
    fun getUserDbController(): UserDbController = userDbController
    fun getDynamicUrlDbController(): DynamicUrlDbController = dynamicUrlDbController
    fun getDynamicUrlDetailsDbController(): DynamicUrlDetailsDbController = dynamicUrlDetailsDBController
}
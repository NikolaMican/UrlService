package di

import dbConection.HikariService
import dynamicUrl.DynamicUrlDbController
import dynamicUrl.DynamicUrlService
import dynamicUrlDetails.DynamicUrlDetailsDbController
import services.HeaderService
import services.LocationService
import services.RandomService

fun di() = DiManager.instance

class DiManager private constructor(){
    companion object {
        val instance: DiManager by lazy {
            DiManager()
        }
    }

    private val randomService = RandomService()
    private val headerService = HeaderService()
    private val locationService = LocationService()
    private val hikariService = HikariService()
    private val dynamicUrlDbController = DynamicUrlDbController(hikariService)
    private val dynamicUrlDetailsDBController = DynamicUrlDetailsDbController(hikariService)
    private val dynamicUrlService = DynamicUrlService(dynamicUrlDbController, dynamicUrlDetailsDBController, randomService)



    fun getRandomService(): RandomService = randomService
    fun getHeaderService(): HeaderService = headerService
    fun getLocationService(): LocationService = locationService
    fun getHikariService(): HikariService = hikariService
    fun getDynamicUrlService(): DynamicUrlService = dynamicUrlService
    fun getDynamicUrlDbController(): DynamicUrlDbController = dynamicUrlDbController
    fun getDynamicUrlDetailsDbController(): DynamicUrlDetailsDbController = dynamicUrlDetailsDBController
}
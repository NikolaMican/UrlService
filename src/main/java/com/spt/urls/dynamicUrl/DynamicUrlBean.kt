package com.spt.urls.dynamicUrl

data class DynamicUrlBean(var idDynamicUrl: Int = 0, var urlId: String, var redirectUrl: String, var numOfClicks: Int) {
    constructor(urlId: String, redirectUrl: String, numOfClicks: Int): this(0, urlId = urlId, redirectUrl = redirectUrl, numOfClicks = numOfClicks)
}
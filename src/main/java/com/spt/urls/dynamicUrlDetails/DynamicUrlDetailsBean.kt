/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spt.urls.dynamicUrlDetails

/**
 *
 * @author Nikola Micanovic
 */
class DynamicUrlDetailsBean {
    var idDynamicUrlDetails = 0
    var fkIdDynamicUrl = 0
    var time: Long = 0
    var location: String? = null
    var browser: String? = null
    var platform: String? = null
    var isMobilePlatform: Boolean = false

    constructor(
        idDynamicUrlDetails: Int,
        fkIdDynamicUrl: Int,
        time: Long,
        location: String?,
        browser: String?,
        platform: String?,
        isMobilePlatform: Boolean
    ) {
        this.idDynamicUrlDetails = idDynamicUrlDetails
        this.fkIdDynamicUrl = fkIdDynamicUrl
        this.time = time
        this.location = location
        this.browser = browser
        this.platform = platform
        this.isMobilePlatform = isMobilePlatform
    }

    constructor(fkIdDynamicUrl: Int, time: Long, location: String?, browser: String?, platform: String?, isMobilePlatform: Boolean) {
        this.fkIdDynamicUrl = fkIdDynamicUrl
        this.time = time
        this.location = location
        this.browser = browser
        this.platform = platform
        this.isMobilePlatform = isMobilePlatform
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spt.urls.dynamicUrlDetails

data class DynamicUrlDetailsBean(
    var idDynamicUrlDetails: Int = 0,
    var fkIdDynamicUrl: Int = 0,
    var time: Long = 0,
    var location: String? = null,
    var browser: String? = null,
    var platform: String? = null,
    var isMobilePlatform: Boolean = false

) {
    constructor(fkIdDynamicUrl: Int, time: Long, location: String?, browser: String?, platform: String?, isMobilePlatform: Boolean):
            this(0, fkIdDynamicUrl, time, location, browser, platform, isMobilePlatform)
}
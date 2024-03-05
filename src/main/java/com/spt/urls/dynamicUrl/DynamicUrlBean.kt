/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spt.urls.dynamicUrl

/**
 *
 * @author Nikola Micanovic
 */
class DynamicUrlBean {
    var idDynamicUrl = 0
    var urlId: String
    var redirectUrl: String
    var numOfClicks: Int

    constructor(idDynamicUrl: Int, urlId: String, redirectUrl: String, numOfClicks: Int) {
        this.idDynamicUrl = idDynamicUrl
        this.urlId = urlId
        this.redirectUrl = redirectUrl
        this.numOfClicks = numOfClicks
    }

    constructor(urlId: String, redirectUrl: String, numOfClicks: Int) {
        this.urlId = urlId
        this.redirectUrl = redirectUrl
        this.numOfClicks = numOfClicks
    }
}
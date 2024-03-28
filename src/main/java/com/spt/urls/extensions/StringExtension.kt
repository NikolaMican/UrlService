package com.spt.urls.extensions

import com.spt.urls.webService.CONF_DOMAIN
import com.spt.urls.webService.MY_IP_ADDRESS
import org.springframework.web.server.ResponseStatusException
import org.springframework.http.HttpStatus.BAD_REQUEST


fun String.getNormalisedUrl(): String {
    if (startsWith("https://") || startsWith("http://")) {
        return this
    }
    return "http://$this"
}

fun String.throwExceptionIfRedirectUrlContainsOurDomain() {
    if (contains(MY_IP_ADDRESS) || contains(CONF_DOMAIN)) {
        throw ResponseStatusException(BAD_REQUEST, "It\'s not allowed to use our domain links in redirectUrl: $this. We prevent infinity loop with it.")
    }
}
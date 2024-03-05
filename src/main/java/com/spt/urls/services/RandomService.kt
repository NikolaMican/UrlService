package com.spt.urls.services

import java.security.SecureRandom

class RandomService {

    private val ALPHANUMERICS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
    private val rnd: SecureRandom = SecureRandom()

    fun randomString(len: Int): String {
        if (len <= 0) {
            throw IllegalArgumentException("len value ($len) has to be positive number")
        }

        val sb = StringBuilder(len)
        for (i in 0 until len) {
            sb.append(ALPHANUMERICS[rnd.nextInt(ALPHANUMERICS.length)])
        }
        return sb.toString()
    }
}
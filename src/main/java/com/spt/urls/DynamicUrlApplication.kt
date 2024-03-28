/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.spt.urls

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import com.spt.urls.services.IpService
import java.util.*


@SpringBootApplication
open class DynamicUrlApplication

val MY_IP_ADDRESS = IpService().getMyIpAddress()

fun main(args: Array<String>) {
    println("========== MY_IP_ADDRESS: $MY_IP_ADDRESS")
//    SpringApplication.run(DynamicUrlApplication::class.java, *args)

    SpringApplication(DynamicUrlApplication::class.java).apply {
        setDefaultProperties(Collections.singletonMap<String, Any>("server.port", "$CONF_PORT"))
    }.run(*args)

}
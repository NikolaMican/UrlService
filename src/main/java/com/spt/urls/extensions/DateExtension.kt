package com.spt.urls.extensions

import java.text.SimpleDateFormat


fun logTime(): String = System.currentTimeMillis().formattedTime()
fun Long.formattedTime(): String = SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS").format(this)
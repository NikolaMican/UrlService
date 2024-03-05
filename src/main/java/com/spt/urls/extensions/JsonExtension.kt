package com.spt.urls.extensions

import com.google.gson.Gson
import java.lang.reflect.Type

private val gson = Gson()

fun Any.toJson(): String = gson.toJson(this)

fun <T> String.fromJson(classOfT: Class<T>): T = gson.fromJson(this, classOfT)
fun <T> String.fromJson(typeOfT: Type): T = gson.fromJson(this, typeOfT)
inline fun <reified T> String.fromJson(): T = fromJson(T::class.java)
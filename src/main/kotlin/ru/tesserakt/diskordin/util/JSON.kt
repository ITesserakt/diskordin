package ru.tesserakt.diskordin.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

fun <T> T.toJson(): String = gson.toJson(this)

fun <T> CharSequence.fromJson(): T = gson.fromJson<T>(this.toString(), object : TypeToken<T>() {}.type)

fun <T> T.toJsonTree(): JsonElement = gson.toJsonTree(this, object : TypeToken<T>() {}.type)
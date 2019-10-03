package ru.tesserakt.diskordin.util

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement

val gson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .create()

fun <T> T.toJson(): String = gson.toJson(this)

inline fun <reified T> CharSequence.fromJson(): T = gson.fromJson<T>(this.toString(), T::class.java)

inline fun <reified T> T.toJsonTree(): JsonElement = gson.toJsonTree(this, T::class.java)
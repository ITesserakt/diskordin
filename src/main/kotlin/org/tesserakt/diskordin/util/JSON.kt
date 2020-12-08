package org.tesserakt.diskordin.util

import arrow.core.Id
import arrow.core.ListK
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.gateway.json.Payload
import org.tesserakt.diskordin.util.typeAdapter.*

val gsonBuilder: GsonBuilder.() -> Unit = {
    setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    registerTypeAdapter(Snowflake::class.java, SnowflakeTypeAdapter())
    registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
    registerTypeAdapter(ListK::class.java, ListKTypeAdapter())
    registerTypeAdapter(Id::class.java, IdTypeAdapter())
    registerTypeAdapter(Payload::class.java, PayloadSerializer())
    serializeNulls()
}

val gson: Gson = GsonBuilder()
    .apply(gsonBuilder)
    .create()

fun <T> T.toJson(): String = gson.toJson(this)

inline fun <reified T> CharSequence.fromJson(): T = gson.fromJson(this.toString(), T::class.java)

fun <T> T.toJsonTree(): JsonElement = gson.toJsonTree(this, object : TypeToken<T>() {}.type)
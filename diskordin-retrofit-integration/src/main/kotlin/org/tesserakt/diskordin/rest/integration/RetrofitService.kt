package org.tesserakt.diskordin.rest.integration

import arrow.core.Eval
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.util.gson
import org.tesserakt.diskordin.util.typeAdapter.SnowflakeTypeAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RetrofitService(private val httpClient: Eval<OkHttpClient>, private val discordApiUrl: String) :
    ReadOnlyProperty<Nothing?, Eval<Retrofit>> {
    override operator fun getValue(thisRef: Nothing?, property: KProperty<*>) = httpClient.map {
        Retrofit.Builder()
            .client(it)
            .baseUrl(discordApiUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(SnowflakeTypeAdapter())
            .build()
    }
}
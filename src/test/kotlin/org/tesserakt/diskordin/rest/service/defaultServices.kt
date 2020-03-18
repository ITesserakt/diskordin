package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallKindAdapterFactory
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import mu.KotlinLogging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.JsonRequest
import org.tesserakt.diskordin.core.entity.builder.BuilderBase
import org.tesserakt.diskordin.util.typeAdapter.IdTypeAdapter
import org.tesserakt.diskordin.util.typeAdapter.InstantTypeAdapter
import org.tesserakt.diskordin.util.typeAdapter.ListKTypeAdapter
import org.tesserakt.diskordin.util.typeAdapter.SnowflakeTypeAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.util.concurrent.TimeUnit

internal val defaultToken = System.getenv("token")

internal val defaultHttpClient = OkHttpClient.Builder()
    .callTimeout(10, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bot $defaultToken")
            .addHeader("User-Agent", "Discord bot (Diskordin, testing env)")
            .build()
        chain.proceed(request)
    }.addInterceptor(
        HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            private val logger = KotlinLogging.logger("[HTTP client]")
            override fun log(message: String) = logger.debug(message)
        }).setLevel(HttpLoggingInterceptor.Level.BODY)
    ).build()

internal val defaultGson = Gson().newBuilder()
    .setPrettyPrinting()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .registerTypeAdapter(Snowflake::class.java, SnowflakeTypeAdapter())
    .registerTypeAdapter(Instant::class.java, InstantTypeAdapter())
    .registerTypeAdapter(ListK::class.java, ListKTypeAdapter())
    .registerTypeAdapter(Id::class.java, IdTypeAdapter())
    .create()

internal val defaultRetrofit = Retrofit.Builder()
    .client(defaultHttpClient)
    .baseUrl("https://discordapp.com/")
    .addConverterFactory(GsonConverterFactory.create(defaultGson))
    .addCallAdapterFactory(CallKindAdapterFactory.create())
    .addConverterFactory(SnowflakeTypeAdapter())
    .build()

internal fun <R : JsonRequest, B : BuilderBase<R>> B.build(f: B.() -> Unit) =
    this.apply(f).create()


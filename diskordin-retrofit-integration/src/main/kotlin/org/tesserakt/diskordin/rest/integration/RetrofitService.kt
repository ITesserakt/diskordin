package org.tesserakt.diskordin.rest.integration

import arrow.core.Eval
import com.tinder.scarlet.utils.getRawType
import okhttp3.OkHttpClient
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.util.gson
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class RetrofitService(private val httpClient: Eval<OkHttpClient>, private val discordApiUrl: String) :
    ReadOnlyProperty<Nothing?, Eval<Retrofit>> {
    override operator fun getValue(thisRef: Nothing?, property: KProperty<*>) = httpClient.map {
        retrofit2.Retrofit.Builder()
            .client(it)
            .baseUrl(discordApiUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addConverterFactory(SnowflakeConverterFactory)
            .build()
    }

    object SnowflakeConverterFactory : Converter.Factory() {
        override fun stringConverter(
            type: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): Converter<Snowflake, String>? = when (type.getRawType()) {
            Snowflake::class.java -> Converter { it.asString() }
            else -> null
        }
    }
}
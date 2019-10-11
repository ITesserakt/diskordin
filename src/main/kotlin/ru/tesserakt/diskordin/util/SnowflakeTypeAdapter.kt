package ru.tesserakt.diskordin.util

import com.google.gson.*
import com.tinder.scarlet.utils.getRawType
import retrofit2.Converter
import retrofit2.Retrofit
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import java.lang.reflect.Type

class SnowflakeTypeAdapter : JsonSerializer<Snowflake>, JsonDeserializer<Snowflake>, Converter.Factory() {
    override fun serialize(src: Snowflake, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        context.serialize(src.asString())

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Snowflake =
        json.asString.asSnowflake()

    override fun stringConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? = when (type.getRawType()) {
        Snowflake::class.java -> Converter<Snowflake, String> { it.asString() }
        else -> null
    }
}
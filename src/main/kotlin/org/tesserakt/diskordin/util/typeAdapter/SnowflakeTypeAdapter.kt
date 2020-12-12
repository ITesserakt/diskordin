package org.tesserakt.diskordin.util.typeAdapter

import com.google.gson.*
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.asSnowflake
import java.lang.reflect.Type

class SnowflakeTypeAdapter : JsonSerializer<Snowflake>, JsonDeserializer<Snowflake> {
    override fun serialize(src: Snowflake, typeOfSrc: Type, context: JsonSerializationContext): JsonElement =
        context.serialize(src.toString())

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Snowflake =
        json.asString.asSnowflake()
}
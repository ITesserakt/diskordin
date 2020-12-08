package org.tesserakt.diskordin.util.typeAdapter

import com.google.gson.*
import kotlinx.datetime.Instant
import java.lang.reflect.Type

class InstantTypeAdapter : JsonDeserializer<Instant>, JsonSerializer<Instant> {
    override fun deserialize(p0: JsonElement, p1: Type, p2: JsonDeserializationContext): Instant =
        Instant.parse(p0.asString)

    override fun serialize(p0: Instant, p1: Type, p2: JsonSerializationContext): JsonElement =
        p2.serialize(p0.toString())
}
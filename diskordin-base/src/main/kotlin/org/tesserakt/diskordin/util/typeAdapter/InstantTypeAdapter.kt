package org.tesserakt.diskordin.util.typeAdapter

import com.google.gson.*
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import java.lang.reflect.Type
import java.time.format.DateTimeFormatter
import java.time.Instant as JInstant

class InstantTypeAdapter : JsonDeserializer<Instant>, JsonSerializer<Instant> {
    override fun deserialize(p0: JsonElement, p1: Type, p2: JsonDeserializationContext): Instant =
        JInstant.from(DateTimeFormatter.ISO_DATE_TIME.parse(p0.asString)).toKotlinInstant()

    override fun serialize(p0: Instant, p1: Type, p2: JsonSerializationContext): JsonElement =
        p2.serialize(p0.toString())
}
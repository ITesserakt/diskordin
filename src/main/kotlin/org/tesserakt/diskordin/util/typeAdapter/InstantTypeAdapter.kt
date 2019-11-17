package org.tesserakt.diskordin.util.typeAdapter

import com.google.gson.*
import org.tesserakt.diskordin.util.toJsonTree
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeFormatter

class InstantTypeAdapter : JsonDeserializer<Instant>, JsonSerializer<Instant> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Instant =
        DateTimeFormatter.ISO_DATE_TIME.parse(json.asString, Instant::from)

    override fun serialize(src: Instant?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement =
        DateTimeFormatter.ISO_DATE_TIME.format(src).toJsonTree()
}
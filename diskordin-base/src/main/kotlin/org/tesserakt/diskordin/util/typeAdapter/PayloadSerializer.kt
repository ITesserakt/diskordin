package org.tesserakt.diskordin.util.typeAdapter

import com.google.gson.*
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.Payload
import java.lang.reflect.Type

class PayloadSerializer : JsonSerializer<Payload<*>> {
    override fun serialize(
        src: Payload<*>,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = JsonObject().also {
        it.add("op", context.serialize(src.opcode))

        if (src.opcode() == Opcode.DISPATCH) {
            it.add("t", context.serialize(src.name!!))
            it.add("s", context.serialize(src.seq!!))
        }

        if (src.opcode() != Opcode.HEARTBEAT)
            it.add("d", src.rawData ?: JsonNull.INSTANCE)
        else it.add("d", src.rawData!!.asJsonObject["value"])
    }
}
package org.tesserakt.diskordin.util.typeAdapter

import arrow.core.Id
import com.google.gson.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class IdTypeAdapter : JsonSerializer<Id<*>>, JsonDeserializer<Id<*>> {
    override fun serialize(src: Id<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val value = src.extract()
        val t = (typeOfSrc as ParameterizedType).actualTypeArguments[0]
        return context.serialize(value, t)
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Id<*> {
        val t = (typeOfT as ParameterizedType).actualTypeArguments[0]
        val value = context.deserialize<Any?>(json, t)
        return Id.just(value)
    }
}
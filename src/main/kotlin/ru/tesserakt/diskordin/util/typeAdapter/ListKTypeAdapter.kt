package ru.tesserakt.diskordin.util.typeAdapter

import arrow.core.ListK
import arrow.core.k
import com.google.gson.*
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ListKTypeAdapter : JsonDeserializer<ListK<*>>, JsonSerializer<ListK<*>> {
    override fun serialize(src: ListK<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val list = src as List<*>
        val t = (typeOfSrc as ParameterizedType).actualTypeArguments[0]
        return context.serialize(list, ParameterizedTypeImpl.make(List::class.java, arrayOf(t), null))
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ListK<*> {
        val t = (typeOfT as ParameterizedType).actualTypeArguments[0]
        val list =
            context.deserialize<List<*>>(json, ParameterizedTypeImpl.make(List::class.java, arrayOf(t), null))
        return list.k()
    }
}
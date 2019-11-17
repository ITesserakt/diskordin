package org.tesserakt.diskordin.util.typeAdapter

import arrow.core.ListK
import arrow.core.k
import com.google.gson.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ListKTypeAdapter : JsonDeserializer<ListK<*>>, JsonSerializer<ListK<*>> {
    override fun serialize(src: ListK<*>, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val list = src as List<*>
        val t = (typeOfSrc as ParameterizedType).actualTypeArguments[0]
        return context.serialize(list, object : ParameterizedType {
            override fun getRawType(): Type = List::class.java

            override fun getOwnerType(): Type? = null

            override fun getActualTypeArguments(): Array<Type> = arrayOf(t)
        })
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ListK<*> {
        val t = (typeOfT as ParameterizedType).actualTypeArguments[0]
        val list =
            context.deserialize<List<*>>(json, object : ParameterizedType {
                override fun getRawType(): Type = List::class.java

                override fun getOwnerType(): Type? = null

                override fun getActualTypeArguments(): Array<Type> = arrayOf(t)
            })
        return list.k()
    }
}
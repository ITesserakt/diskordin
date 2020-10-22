package org.tesserakt.diskordin.commands.resolver

import org.tesserakt.diskordin.commands.CommandContext
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

class ResolversProvider(private val resolvers: Map<KClass<*>, TypeResolver<*, *>>) {
    @Suppress("UNCHECKED_CAST")
    @PublishedApi
    internal fun <T : Any> get(clazz: KClass<T>) = resolvers[clazz] as TypeResolver<T, *>

    val size = resolvers.size
    val types = resolvers.keys

    @Suppress("UNCHECKED_CAST")
    inline operator fun <reified T : Any, F, C : CommandContext> getValue(thisRef: Nothing?, prop: KProperty<*>) =
        get(T::class) as? TypeResolver<T, C>
}
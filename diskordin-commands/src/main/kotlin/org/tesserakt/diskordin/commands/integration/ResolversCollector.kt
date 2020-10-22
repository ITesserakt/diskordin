package org.tesserakt.diskordin.commands.integration

import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.resolver.*
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

@Suppress("NOTHING_TO_INLINE")
@RequestBuilder
class ResolversCollector {
    private val builtInResolvers = {
        String::class bindWith StringResolver()
        Char::class bindWith CharResolver()
        Boolean::class bindWith BooleanResolver()
        Int::class bindWith IntResolver()
        Float::class bindWith FloatResolver()
        Long::class bindWith LongResolver()
        Short::class bindWith ShortResolver()
        Double::class bindWith DoubleResolver()
        BigDecimal::class bindWith BigDecimalResolver()
        BigInteger::class bindWith BigIntegerResolver()
        Byte::class bindWith ByteResolver()
        IUser::class bindWith UserResolver()
    }

    private val _resolvers = mutableMapOf<KClass<*>, TypeResolver<*, *>>()
    internal val resolvers: Map<KClass<*>, TypeResolver<*, *>> = _resolvers

    init {
        builtInResolvers()
    }

    infix fun <T : Any, C : CommandContext> KClass<T>.bindWith(resolver: TypeResolver<T, C>) {
        _resolvers += this to resolver
    }
}

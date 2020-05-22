package org.tesserakt.diskordin.commands.integration

import arrow.fx.typeclasses.Async
import org.tesserakt.diskordin.commands.CommandContext
import org.tesserakt.diskordin.commands.resolver.*
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.builder.RequestBuilder
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

@Suppress("NOTHING_TO_INLINE")
@RequestBuilder
class ResolversCollector<F>(A: Async<F>) {
    private val builtInResolvers = {
        String::class bindWith StringResolver(A)
        Char::class bindWith CharResolver(A)
        Boolean::class bindWith BooleanResolver(A)
        Int::class bindWith IntResolver(A)
        Float::class bindWith FloatResolver(A)
        Long::class bindWith LongResolver(A)
        Short::class bindWith ShortResolver(A)
        Double::class bindWith DoubleResolver(A)
        BigDecimal::class bindWith BigDecimalResolver(A)
        BigInteger::class bindWith BigIntegerResolver(A)
        Byte::class bindWith ByteResolver(A)
        IUser::class bindWith UserResolver(A)
    }

    private val _resolvers = mutableMapOf<KClass<*>, TypeResolver<*, *, *>>()
    internal val resolvers: Map<KClass<*>, TypeResolver<*, *, *>> = _resolvers

    init {
        builtInResolvers()
    }

    infix fun <T : Any, F, C : CommandContext<F>> KClass<T>.bindWith(resolver: TypeResolver<T, F, C>) {
        _resolvers += this to resolver
    }
}

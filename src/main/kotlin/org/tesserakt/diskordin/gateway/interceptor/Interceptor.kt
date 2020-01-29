package org.tesserakt.diskordin.gateway.interceptor

import kotlin.reflect.KClass

interface Interceptor<T : Interceptor.Context> : (T) -> Unit {
    abstract class Context

    val selfContext: KClass<T>

    override fun invoke(p1: T) = intercept(p1)
    fun intercept(context: T)
}
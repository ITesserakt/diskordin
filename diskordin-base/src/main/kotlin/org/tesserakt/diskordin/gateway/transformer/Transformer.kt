package org.tesserakt.diskordin.gateway.transformer

interface Transformer<in T, out R> : (T) -> R {
    override fun invoke(p1: T): R = transform(p1)
    fun transform(context: T): R
}
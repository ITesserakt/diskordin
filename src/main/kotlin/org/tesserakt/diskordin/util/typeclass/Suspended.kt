package org.tesserakt.diskordin.util.typeclass

import arrow.Kind

interface Suspended<F> {
    suspend fun <A> Kind<F, A>.suspended(): A
}
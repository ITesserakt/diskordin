package org.tesserakt.diskordin.impl.util.typeclass

import arrow.Kind
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.fix
import org.tesserakt.diskordin.util.typeclass.Suspended

interface IOSuspended : Suspended<ForIO> {
    override suspend fun <A> Kind<ForIO, A>.suspended(): A = fix().suspended()
}

private val ioSuspended: Suspended<ForIO> = object : IOSuspended {}
fun IO.Companion.suspended() = ioSuspended
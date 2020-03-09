package org.tesserakt.diskordin.commands.util

import arrow.fx.IO
import arrow.fx.typeclasses.Async

fun <F, A> IO<A>.fromIO(AC: Async<F>) = AC.effect { suspended() }
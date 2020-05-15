package org.tesserakt.diskordin.core.data

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.andThen
import arrow.fx.ForIO
import arrow.typeclasses.Comonad
import arrow.ui.Store

typealias IdentifiedF <F, E> = Store<Snowflake, out Kind<F, E>>
typealias Identified <E> = IdentifiedF<ForId, E>
typealias IdentifiedIO <E> = IdentifiedF<ForIO, E>

@JvmName("identifyHK")
infix fun <F, E> Snowflake.identify(render: (Snowflake) -> Kind<F, E>): IdentifiedF<F, E> = Store(this, render)
infix fun <E> Snowflake.identify(render: (Snowflake) -> E): Identified<E> = Store(this, render andThen ::Id)
inline val IdentifiedF<*, *>.id get() = state
operator fun <F, E> IdentifiedF<F, E>.invoke() = extract()
operator fun <F, E> IdentifiedF<F, E>.invoke(CM: Comonad<F>) = CM.run { extract().extract() }
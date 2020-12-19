@file:Suppress("DEPRECATION")

package org.tesserakt.diskordin.core.data

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.andThen
import arrow.core.extensions.id.comonad.extract
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.fix
import arrow.typeclasses.Comonad
import arrow.ui.Store

typealias IdentifiedF <F, E> = Store<Snowflake, out Kind<F, E>>
typealias Identified <E> = IdentifiedF<ForId, E>
typealias IdentifiedIO <E> = IdentifiedF<ForIO, E>

infix fun <E> Snowflake.identify(render: suspend (Snowflake) -> E): IdentifiedIO<E> =
    Store(this) { IO { render(this) } }

infix fun <F, E> Snowflake.identify(render: (Snowflake) -> Kind<F, E>): IdentifiedF<F, E> = Store(this, render)
infix fun <E> Snowflake.identifyId(render: (Snowflake) -> E): Identified<E> = Store(this, render andThen ::Id)
inline val Store<Snowflake, *>.id get() = state
suspend operator fun <E> IdentifiedIO<E>.invoke() = extract().fix().suspended()
operator fun <E> Identified<E>.invoke() = extract().extract()
operator fun <F, E> IdentifiedF<F, E>.invoke() = extract()
operator fun <F, E> IdentifiedF<F, E>.invoke(CM: Comonad<F>) = CM.run { extract().extract() }
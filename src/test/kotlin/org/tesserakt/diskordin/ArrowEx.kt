package org.tesserakt.diskordin

import arrow.Kind
import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.Id
import arrow.core.extensions.id.comonad.comonad
import arrow.core.orNull
import arrow.fx.ForIO
import arrow.fx.extensions.io.monad.map
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ExitCase
import arrow.integrations.retrofit.adapter.CallK
import org.tesserakt.diskordin.core.data.json.response.DiscordResponse
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.rest.RestClient

internal fun <E, A> EitherOf<E, A>.getLeft(): E {
    require(this is Either.Left<E>) { "Cannot get left value from Right Either" }
    return swap().orNull()!!
}

internal fun <E, A> EitherOf<E, A>.getRight(): A {
    require(this is Either.Right<A>) { "Cannot get right value from Left Either" }
    return orNull()!!
}

internal fun <E : IDiscordObject, R : DiscordResponse<E, *>> RestClient<ForIO>.callAndExtract(
    f: RestClient<ForIO>.() -> CallK<Id<R>>
) = Id.comonad().run {
    this@callAndExtract.callRaw(f)
}.map { it.extract() }.attempt()


internal fun <F, A> Bracket<F, Throwable>.withFinalize(
    init: Kind<F, A>,
    use: (A) -> Unit,
    finalize: (A) -> Kind<F, *>
) = init.bracketCase(use = { use(it).just() }, release = { r, case ->
    when (case) {
        is ExitCase.Completed -> finalize(r).map { Unit }
        is ExitCase.Error<Throwable> -> when (case.e) {
            is AssertionError -> finalize(r).map { Unit }
            else -> raiseError(case.e)
        }
        is ExitCase.Canceled -> Unit.just()
    }
})
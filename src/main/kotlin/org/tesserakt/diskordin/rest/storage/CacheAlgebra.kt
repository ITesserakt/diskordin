package org.tesserakt.diskordin.rest.storage

import arrow.core.*
import arrow.free.Free
import arrow.free.extensions.FreeMonad
import arrow.free.fix
import arrow.free.foldMap
import arrow.higherkind
import arrow.typeclasses.Monad
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity

@higherkind
sealed class CacheF<out R> : CacheFOf<R> {
    data class Put<A : IEntity>(val value: A) : CacheF<Unit>()
    data class Get<A : IEntity>(val id: Snowflake) : CacheF<Option<A>>()
    data class Delete<A : IEntity>(val id: Snowflake) : CacheF<Option<A>>()
    object Invalidate : CacheF<Unit>()
    companion object : FreeMonad<ForCacheF>
}

typealias Cache<A> = Free<ForCacheF, A>

fun <A : IEntity> put(value: A): Cache<Unit> = Free.liftF(CacheF.Put(value))

fun <A : IEntity> get(id: Snowflake): Cache<Option<A>> = Free.liftF(CacheF.Get(id))

fun <A : IEntity> putAndGet(value: A): Cache<A> = CacheF.fx.monad {
    !put(value)
    get<A>(value.id).bind().getOrElse { value }
}.fix()

fun <A : IEntity> getOrPut(id: Snowflake, block: () -> A): Cache<A> = CacheF.fx.monad {
    when (val cached = !get<A>(id)) {
        is None -> !putAndGet(block())
        is Some<A> -> cached.t
    }
}.fix()

fun <A : IEntity> delete(id: Snowflake): Cache<Option<A>> = Free.liftF(CacheF.Delete(id))

fun invalidate(): Cache<Unit> = Free.liftF(CacheF.Invalidate)

fun <A, F> Cache<A>.run(interpreter: FunctionK<ForCacheF, F>, M: Monad<F>) = this.foldMap(interpreter, M)
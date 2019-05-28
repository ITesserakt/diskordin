package ru.tesserakt.diskordin.core.cache

import kotlinx.coroutines.Deferred
import org.kodein.di.generic.factory
import ru.tesserakt.diskordin.core.client.Diskordin
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.`object`.IInvite
import ru.tesserakt.diskordin.util.Identified
import kotlin.reflect.KClass

typealias Cache <T> = MutableList<Identified<T>>

sealed class CacheBase<T>(cache: Cache<out T> = mutableListOf()) :
    MutableMap<Snowflake, Deferred<T>> by cache.associateBy(
        { it.state },
        { it.extractAsync() }
    ).toMutableMap()

class ChannelCache internal constructor() : CacheBase<IChannel>()
class GuildCache internal constructor() : CacheBase<IGuild>()
class EmojiCache internal constructor() : CacheBase<IEmoji>()
class InviteCache internal constructor() : CacheBase<IInvite>()
class UserCache internal constructor() : CacheBase<IUser>()
class ObjectCache<T : IEntity> internal constructor(type: KClass<T>) : CacheBase<T>()

private val genericCacheFun by Diskordin.kodein.factory<KClass<out IEntity>, ObjectCache<out IEntity>>()
val <T : IEntity> KClass<T>.genericCache: ObjectCache<T>
    get() = genericCacheFun(this) as ObjectCache<T>
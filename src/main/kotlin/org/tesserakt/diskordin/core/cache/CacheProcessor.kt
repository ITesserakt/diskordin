package org.tesserakt.diskordin.core.cache

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.tesserakt.diskordin.core.cache.CacheSnapshotBuilder.Companion.mutate
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.data.EntitySifter
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.event.PresenceUpdateEvent
import org.tesserakt.diskordin.core.data.event.UserUpdateEvent
import org.tesserakt.diskordin.core.data.event.guild.GuildCreateEvent
import org.tesserakt.diskordin.core.data.event.guild.GuildDeleteEvent
import org.tesserakt.diskordin.core.data.event.guild.GuildUpdateEvent
import org.tesserakt.diskordin.core.data.event.message.MessageCreateEvent
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.entity.IDiscordObject
import org.tesserakt.diskordin.core.entity.IEntity
import org.tesserakt.diskordin.core.entity.IGuild
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild
import kotlin.reflect.KClass

@ExperimentalCoroutinesApi
internal class CacheProcessor(
    private val updaters: Map<KClass<*>, CacheUpdater<*>>,
    private val deleters: Map<KClass<*>, CacheDeleter<*>>,
    private val sifter: EntitySifter
) : EventInterceptor(), BootstrapContext.ExtensionContext {
    private val _state: MutableStateFlow<CacheSnapshot> = MutableStateFlow(MemoryCacheSnapshot.empty())
    val state = _state.asStateFlow()

    inline fun <reified T : IDiscordObject> updateData(data: T) = updateData(data, T::class)

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> updateData(data: T, clazz: KClass<T>) {
        val handler = updaters[clazz] as CacheUpdater<T>

        if (sifter.isAllowed(data)) _state.value = handler.handleAndGet(state.value.mutate(), data)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : IEntity> deleteData(id: Snowflake) {
        val handler = deleters[T::class] as CacheDeleter<T>

        _state.value = state.value.mutate().also {
            handler.delete(it, id)
        }
    }

    private fun clean() {
        _state.value = MemoryCacheSnapshot.empty()
    }

    override suspend fun Context.guildCreate(event: GuildCreateEvent) = updateData(event.guild())
    override suspend fun Context.presenceUpdate(event: PresenceUpdateEvent) = updateData(event.user())
    override suspend fun Context.userUpdate(event: UserUpdateEvent) = updateData(event.user())
    override suspend fun Context.guildUpdate(event: GuildUpdateEvent) = updateData(event.guild())
    override suspend fun Context.messageCreate(event: MessageCreateEvent) = updateData(event.message())
    override suspend fun Context.guildDelete(event: GuildDeleteEvent) {
        if (event.isUnavailable) updateData(UnavailableGuild(event.guildId, false))
        deleteData<IGuild>(event.guildId)
    }

    companion object : BootstrapContext.PersistentExtension<CacheProcessor>
}
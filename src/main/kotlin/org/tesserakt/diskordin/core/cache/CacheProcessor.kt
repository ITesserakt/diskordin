package org.tesserakt.diskordin.core.cache

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.tesserakt.diskordin.core.cache.CacheSnapshotBuilder.Companion.mutate
import org.tesserakt.diskordin.core.cache.handler.CacheDeleter
import org.tesserakt.diskordin.core.cache.handler.CacheUpdater
import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.data.EntitySifter
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.event.PresenceUpdateEvent
import org.tesserakt.diskordin.core.data.event.UserUpdateEvent
import org.tesserakt.diskordin.core.data.event.channel.ChannelCreateEvent
import org.tesserakt.diskordin.core.data.event.channel.ChannelDeleteEvent
import org.tesserakt.diskordin.core.data.event.channel.ChannelUpdateEvent
import org.tesserakt.diskordin.core.data.event.guild.*
import org.tesserakt.diskordin.core.data.event.lifecycle.InvalidSessionEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import org.tesserakt.diskordin.core.data.event.message.MessageBulkDeleteEvent
import org.tesserakt.diskordin.core.data.event.message.MessageCreateEvent
import org.tesserakt.diskordin.core.data.event.message.MessageDeleteEvent
import org.tesserakt.diskordin.core.data.event.message.MessageUpdateEvent
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.gateway.interceptor.EventInterceptor
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild
import kotlin.reflect.KClass

internal class CacheProcessor(
    private val updaters: Map<KClass<*>, CacheUpdater<*>>,
    private val deleters: Map<KClass<*>, CacheDeleter<*>>,
    private val sifter: EntitySifter
) : EventInterceptor(), BootstrapContext.ExtensionContext {
    private val _state: MutableStateFlow<CacheSnapshot> = MutableStateFlow(MemoryCacheSnapshot.empty())
    val state = _state.asStateFlow()

    @Suppress("UNCHECKED_CAST")
    fun <T : IDiscordObject> updateData(data: T) {
        val handler =
            updaters.filterKeys { it.java.isAssignableFrom(data::class.java) }.values.first() as CacheUpdater<T>
        if (sifter.isAllowed(data)) _state.value = handler.handleAndGet(state.value.mutate(), data)
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : IEntity> deleteData(id: Snowflake) {
        val handler = deleters.filterKeys { it.java.isAssignableFrom(T::class.java) }.values.first() as CacheDeleter<T>

        _state.value = state.value.mutate().also {
            handler.delete(it, id)
        }
    }

    private fun clean() {
        _state.value = MemoryCacheSnapshot.empty()
    }

    fun <T : Any> hasHandler(clazz: KClass<T>) =
        (updaters + deleters).filterKeys { it.java.isAssignableFrom(clazz.java) }.isNotEmpty()

    override suspend fun Context.guildCreate(event: GuildCreateEvent) = updateData(event.guild())
    override suspend fun Context.presenceUpdate(event: PresenceUpdateEvent) = updateData(event.user())
    override suspend fun Context.userUpdate(event: UserUpdateEvent) = updateData(event.user())
    override suspend fun Context.guildUpdate(event: GuildUpdateEvent) = updateData(event.guild())
    override suspend fun Context.channelCreate(event: ChannelCreateEvent) = updateData(event.channel())
    override suspend fun Context.channelUpdate(event: ChannelUpdateEvent) = updateData(event.channel())
    override suspend fun Context.channelDelete(event: ChannelDeleteEvent) = deleteData<IChannel>(event.channel.id)
    override suspend fun Context.memberUpdate(event: MemberUpdateEvent) = updateData(event.user())
    override suspend fun Context.ban(event: BanEvent) = updateData(event.user())
    override suspend fun Context.memberChunk(event: MemberChunkEvent) = event.members.forEach(::updateData)
    override suspend fun Context.roleCreate(event: RoleCreateEvent) = updateData(event.role())
    override suspend fun Context.roleDelete(event: RoleDeleteEvent) = deleteData<IRole>(event.roleId)
    override suspend fun Context.roleUpdate(event: RoleUpdateEvent) = updateData(event.role())
    override suspend fun Context.unban(event: UnbanEvent) = updateData(event.user())
    override suspend fun Context.messageDelete(event: MessageDeleteEvent) = deleteData<IMessage>(event.messageId)
    override suspend fun Context.messageUpdate(event: MessageUpdateEvent) = updateData(event.message())
    override suspend fun Context.invalidSession(event: InvalidSessionEvent) = clean()

    override suspend fun Context.messageCreate(event: MessageCreateEvent) {
        updateData(event.message())
        event.author?.invoke()?.let(::updateData)
    }

    override suspend fun Context.messageBulkDelete(event: MessageBulkDeleteEvent) =
        event.deletedMessages.forEach { deleteData<IMessage>(it) }

    override suspend fun Context.emojisUpdate(event: EmojisUpdateEvent) {
        //TODO handle emojis updates
    }

    override suspend fun Context.ready(event: ReadyEvent) {
        updateData(event.self())
        //event.guilds.map { it.id }.parTraverseN(2) { event.client.getGuild(it) }
    }

    override suspend fun Context.guildDelete(event: GuildDeleteEvent) {
        if (event.isUnavailable) updateData(UnavailableGuild(event.guildId, false))
        deleteData<IGuild>(event.guildId)
    }

    override suspend fun Context.memberJoin(event: MemberJoinEvent) {
        updateData(event.member())
        updateData(event.user())
    }

    override suspend fun Context.memberRemove(event: MemberRemoveEvent) {
        deleteData<IMember>(event.user.id)
        updateData(event.user())
    }

    companion object : BootstrapContext.PersistentExtension<CacheProcessor>
}
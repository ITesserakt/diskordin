package org.tesserakt.diskordin.gateway.interceptor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.tesserakt.diskordin.core.data.event.*
import org.tesserakt.diskordin.core.data.event.channel.ChannelCreateEvent
import org.tesserakt.diskordin.core.data.event.channel.ChannelDeleteEvent
import org.tesserakt.diskordin.core.data.event.channel.ChannelPinsUpdateEvent
import org.tesserakt.diskordin.core.data.event.channel.ChannelUpdateEvent
import org.tesserakt.diskordin.core.data.event.guild.*
import org.tesserakt.diskordin.core.data.event.lifecycle.*
import org.tesserakt.diskordin.core.data.event.message.MessageBulkDeleteEvent
import org.tesserakt.diskordin.core.data.event.message.MessageCreateEvent
import org.tesserakt.diskordin.core.data.event.message.MessageDeleteEvent
import org.tesserakt.diskordin.core.data.event.message.MessageUpdateEvent
import org.tesserakt.diskordin.core.data.event.message.reaction.AllReactionsRemoveEvent
import org.tesserakt.diskordin.core.data.event.message.reaction.ReactionAddEvent
import org.tesserakt.diskordin.core.data.event.message.reaction.ReactionRemoveEvent
import org.tesserakt.diskordin.gateway.json.commands.GatewayCommand
import org.tesserakt.diskordin.gateway.sendPayload
import org.tesserakt.diskordin.gateway.shard.Shard
import org.tesserakt.diskordin.gateway.shard.ShardController
import kotlin.reflect.KClass

@ExperimentalCoroutinesApi
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
abstract class EventInterceptor : Interceptor<EventInterceptor.Context> {
    @ExperimentalCoroutinesApi
    class Context constructor(
        val event: IEvent,
        controller: ShardController,
        shard: Shard
    ) : Interceptor.Context(controller, shard)

    override val selfContext: KClass<Context> = Context::class

    open suspend fun Context.allReactionsRemove(event: AllReactionsRemoveEvent) {}
    open suspend fun Context.ban(event: BanEvent) {}
    open suspend fun Context.channelCreate(event: ChannelCreateEvent) {}
    open suspend fun Context.channelDelete(event: ChannelDeleteEvent) {}
    open suspend fun Context.channelUpdate(event: ChannelUpdateEvent) {}
    open suspend fun Context.unban(event: UnbanEvent) {}
    open suspend fun Context.channelPinsUpdate(event: ChannelPinsUpdateEvent) {}
    open suspend fun Context.emojisUpdate(event: EmojisUpdateEvent) {}
    open suspend fun Context.guildCreate(event: GuildCreateEvent) {}
    open suspend fun Context.guildDelete(event: GuildDeleteEvent) {}
    open suspend fun Context.guildUpdate(event: GuildUpdateEvent) {}
    open suspend fun Context.heartbeatACK(event: HeartbeatACKEvent) {}
    open suspend fun Context.heartbeat(event: HeartbeatEvent) {}
    open suspend fun Context.invalidSession(event: InvalidSessionEvent) {}
    open suspend fun Context.reconnect(event: ReconnectEvent) {}
    open suspend fun Context.resumed(event: ResumedEvent) {}
    open suspend fun Context.messageCreate(event: MessageCreateEvent) {}
    open suspend fun Context.messageUpdate(event: MessageUpdateEvent) {}
    open suspend fun Context.hello(event: HelloEvent) {}
    open suspend fun Context.integrationsUpdate(event: IntegrationsUpdateEvent) {}
    open suspend fun Context.memberChunk(event: MemberChunkEvent) {}
    open suspend fun Context.memberJoin(event: MemberJoinEvent) {}
    open suspend fun Context.memberRemove(event: MemberRemoveEvent) {}
    open suspend fun Context.memberUpdate(event: MemberUpdateEvent) {}
    open suspend fun Context.messageBulkDelete(event: MessageBulkDeleteEvent) {}
    open suspend fun Context.messageDelete(event: MessageDeleteEvent) {}
    open suspend fun Context.presenceUpdate(event: PresenceUpdateEvent) {}
    open suspend fun Context.reactionAdd(event: ReactionAddEvent) {}
    open suspend fun Context.reactionRemove(event: ReactionRemoveEvent) {}
    open suspend fun Context.ready(event: ReadyEvent) {}
    open suspend fun Context.roleCreate(event: RoleCreateEvent) {}
    open suspend fun Context.roleDelete(event: RoleDeleteEvent) {}
    open suspend fun Context.roleUpdate(event: RoleUpdateEvent) {}
    open suspend fun Context.typing(event: TypingEvent) {}
    open suspend fun Context.userUpdate(event: UserUpdateEvent) {}
    open suspend fun Context.voiceServerUpdate(event: VoiceServerUpdateEvent) {}
    open suspend fun Context.voiceStateUpdate(event: VoiceStateUpdateEvent) {}
    open suspend fun Context.webhooksUpdate(event: WebhooksUpdateEvent) {}

    override suspend fun intercept(context: Context) = context.run {
        when (val e = context.event) {
            is AllReactionsRemoveEvent -> allReactionsRemove(e)
            is BanEvent -> ban(e)
            is UnbanEvent -> unban(e)
            is ChannelCreateEvent -> channelCreate(e)
            is ChannelUpdateEvent -> channelUpdate(e)
            is ChannelDeleteEvent -> channelDelete(e)
            is ChannelPinsUpdateEvent -> channelPinsUpdate(e)
            is GuildCreateEvent -> guildCreate(e)
            is GuildUpdateEvent -> guildUpdate(e)
            is GuildDeleteEvent -> guildDelete(e)
            is InvalidSessionEvent -> invalidSession(e)
            is ReconnectEvent -> reconnect(e)
            is ResumedEvent -> resumed(e)
            is MessageCreateEvent -> messageCreate(e)
            is MessageUpdateEvent -> messageUpdate(e)
            is EmojisUpdateEvent -> emojisUpdate(e)
            is HeartbeatACKEvent -> heartbeatACK(e)
            is HelloEvent -> hello(e)
            is IntegrationsUpdateEvent -> integrationsUpdate(e)
            is MemberChunkEvent -> memberChunk(e)
            is MemberJoinEvent -> memberJoin(e)
            is MemberRemoveEvent -> memberRemove(e)
            is MemberUpdateEvent -> memberUpdate(e)
            is MessageBulkDeleteEvent -> messageBulkDelete(e)
            is MessageDeleteEvent -> messageDelete(e)
            is PresenceUpdateEvent -> presenceUpdate(e)
            is ReactionAddEvent -> reactionAdd(e)
            is ReactionRemoveEvent -> reactionRemove(e)
            is ReadyEvent -> ready(e)
            is RoleCreateEvent -> roleCreate(e)
            is RoleDeleteEvent -> roleDelete(e)
            is RoleUpdateEvent -> roleUpdate(e)
            is TypingEvent -> typing(e)
            is UserUpdateEvent -> userUpdate(e)
            is VoiceServerUpdateEvent -> voiceServerUpdate(e)
            is VoiceStateUpdateEvent -> voiceStateUpdate(e)
            is WebhooksUpdateEvent -> webhooksUpdate(e)
            else -> throw IllegalStateException("Unexpected event happened: $e")
        }
    }
}

@ExperimentalCoroutinesApi
@Suppress("NOTHING_TO_INLINE")
internal suspend inline fun EventInterceptor.Context.sendPayload(data: GatewayCommand) =
    shard.lifecycle.connection.sendPayload(data, shard.sequence.value, shard.shardData.index)
package org.tesserakt.diskordin.gateway.interceptor

import arrow.Kind
import arrow.fx.typeclasses.Concurrent
import arrow.typeclasses.ApplicativeError
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

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
abstract class EventInterceptor<F>(AE: ApplicativeError<F, Throwable>) : Interceptor<EventInterceptor.Context, F>,
    ApplicativeError<F, Throwable> by AE {
    class Context(
        val event: IEvent,
        controller: ShardController,
        shard: Shard
    ) : Interceptor.Context(controller, shard)

    override val selfContext: KClass<Context> = Context::class

    open fun Context.allReactionsRemove(event: AllReactionsRemoveEvent): Kind<F, Unit> = Unit.just()
    open fun Context.ban(event: BanEvent): Kind<F, Unit> = Unit.just()
    open fun Context.chanelCreate(event: ChannelCreateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.channelDelete(event: ChannelDeleteEvent): Kind<F, Unit> = Unit.just()
    open fun Context.channelUpdate(event: ChannelUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.unban(event: UnbanEvent): Kind<F, Unit> = Unit.just()
    open fun Context.channelPinsUpdate(event: ChannelPinsUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.emojisUpdate(event: EmojisUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.guildCreate(event: GuildCreateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.guildDelete(event: GuildDeleteEvent): Kind<F, Unit> = Unit.just()
    open fun Context.guildUpdate(event: GuildUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.heartbeatACK(event: HeartbeatACKEvent): Kind<F, Unit> = Unit.just()
    open fun Context.heartbeat(event: HeartbeatEvent): Kind<F, Unit> = Unit.just()
    open fun Context.invalidSession(event: InvalidSessionEvent): Kind<F, Unit> = Unit.just()
    open fun Context.reconnect(event: ReconnectEvent): Kind<F, Unit> = Unit.just()
    open fun Context.resumed(event: ResumedEvent): Kind<F, Unit> = Unit.just()
    open fun Context.messageCreate(event: MessageCreateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.messageUpdate(event: MessageUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.hello(event: HelloEvent): Kind<F, Unit> = Unit.just()
    open fun Context.integrationsUpdate(event: IntegrationsUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.memberChunk(event: MemberChunkEvent): Kind<F, Unit> = Unit.just()
    open fun Context.memberJoin(event: MemberJoinEvent): Kind<F, Unit> = Unit.just()
    open fun Context.memberRemove(event: MemberRemoveEvent): Kind<F, Unit> = Unit.just()
    open fun Context.memberUpdate(event: MemberUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.messageBulkDelete(event: MessageBulkDeleteEvent): Kind<F, Unit> = Unit.just()
    open fun Context.messageDelete(event: MessageDeleteEvent): Kind<F, Unit> = Unit.just()
    open fun Context.presenceUpdate(event: PresenceUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.reactionAdd(event: ReactionAddEvent): Kind<F, Unit> = Unit.just()
    open fun Context.reactionRemove(event: ReactionRemoveEvent): Kind<F, Unit> = Unit.just()
    open fun Context.ready(event: ReadyEvent): Kind<F, Unit> = Unit.just()
    open fun Context.roleCreate(event: RoleCreateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.roleDelete(event: RoleDeleteEvent): Kind<F, Unit> = Unit.just()
    open fun Context.roleUpdate(event: RoleUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.typing(event: TypingEvent): Kind<F, Unit> = Unit.just()
    open fun Context.userUpdate(event: UserUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.voiceServerUpdate(event: VoiceServerUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.voiceStateUpdate(event: VoiceStateUpdateEvent): Kind<F, Unit> = Unit.just()
    open fun Context.webhooksUpdate(event: WebhooksUpdateEvent): Kind<F, Unit> = Unit.just()

    override fun intercept(context: Context) = context.run {
        when (val e = context.event) {
            is AllReactionsRemoveEvent -> allReactionsRemove(e)
            is BanEvent -> ban(e)
            is UnbanEvent -> unban(e)
            is ChannelCreateEvent -> chanelCreate(e)
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
            else -> raiseError(IllegalStateException("Unexpected event happened: $e"))
        }
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <F> EventInterceptor.Context.sendPayload(data: GatewayCommand, CC: Concurrent<F>) =
    shard.connection.sendPayload(data, shard.sequence, shard.shardData.current, CC)
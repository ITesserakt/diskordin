package org.tesserakt.diskordin.gateway.interceptor

import org.tesserakt.diskordin.core.data.event.*
import org.tesserakt.diskordin.core.data.event.channel.ChannelPinsUpdateEvent
import org.tesserakt.diskordin.core.data.event.guild.*
import org.tesserakt.diskordin.core.data.event.lifecycle.HeartbeatACKEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.HelloEvent
import org.tesserakt.diskordin.core.data.event.lifecycle.ReadyEvent
import org.tesserakt.diskordin.core.data.event.message.MessageBulkDeleteEvent
import org.tesserakt.diskordin.core.data.event.message.MessageDeleteEvent
import org.tesserakt.diskordin.core.data.event.message.reaction.AllReactionsRemoveEvent
import org.tesserakt.diskordin.core.data.event.message.reaction.ReactionAddEvent
import kotlin.reflect.KClass

abstract class EventInterceptor : Interceptor<EventInterceptor.Context> {
    data class Context(
        val event: IEvent
    ) : Interceptor.Context()

    override val selfContext: KClass<Context> = Context::class

    open fun allReactionsRemove(event: AllReactionsRemoveEvent) {}
    open fun ban(event: BanEvent) {}
    open fun channelPinsUpdate(event: ChannelPinsUpdateEvent) {}
    open fun emojisUpdate(event: EmojisUpdateEvent) {}
    open fun heartbeatACK(event: HeartbeatACKEvent) {}
    open fun hello(event: HelloEvent) {}
    open fun integrationsUpdate(event: IntegrationsUpdateEvent) {}
    open fun memberChunk(event: MemberChunkEvent) {}
    open fun memberJoin(event: MemberJoinEvent) {}
    open fun memberRemove(event: MemberRemoveEvent) {}
    open fun memberUpdate(event: MemberUpdateEvent) {}
    open fun messageBulkDelete(event: MessageBulkDeleteEvent) {}
    open fun messageDelete(event: MessageDeleteEvent) {}
    open fun presenceUpdate(event: PresenceUpdateEvent) {}
    open fun reactionAdd(event: ReactionAddEvent) {}
    open fun ready(event: ReadyEvent) {}
    open fun roleCreate(event: RoleCreateEvent) {}
    open fun roleDelete(event: RoleDeleteEvent) {}
    open fun roleUpdate(event: RoleUpdateEvent) {}
    open fun typing(event: TypingEvent) {}
    open fun voiceServerUpdate(event: VoiceServerUpdateEvent) {}
    open fun webhooksUpdate(event: WebhooksUpdateEvent) {}

    override fun intercept(context: Context) = when (val e = context.event) {
        is AllReactionsRemoveEvent -> allReactionsRemove(e)
        is BanEvent -> ban(e)
        is ChannelPinsUpdateEvent -> channelPinsUpdate(e)
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
        is ReadyEvent -> ready(e)
        is RoleCreateEvent -> roleCreate(e)
        is RoleDeleteEvent -> roleDelete(e)
        is RoleUpdateEvent -> roleUpdate(e)
        is TypingEvent -> typing(e)
        is VoiceServerUpdateEvent -> voiceServerUpdate(e)
        is WebhooksUpdateEvent -> webhooksUpdate(e)
        else -> throw IllegalStateException("Should never raises")
    }
}
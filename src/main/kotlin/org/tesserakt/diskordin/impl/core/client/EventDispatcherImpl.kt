package org.tesserakt.diskordin.impl.core.client

import arrow.core.extensions.either.monad.flatTap
import arrow.core.left
import arrow.core.right
import arrow.fx.typeclasses.Async
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.sendBlocking
import org.tesserakt.diskordin.core.client.EventDispatcher
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
import org.tesserakt.diskordin.gateway.json.IRawEvent
import org.tesserakt.diskordin.gateway.json.Opcode
import org.tesserakt.diskordin.gateway.json.Payload

@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
internal class EventDispatcherImpl<F>(private val A: Async<F>) : EventDispatcher<F>(), Async<F> by A {
    @ExperimentalCoroutinesApi
    private val channel = ConflatedBroadcastChannel<IEvent>()

    @ExperimentalCoroutinesApi
    override fun publish(rawEvent: Payload<IRawEvent>) =
        parseEvent(rawEvent).flatTap { channel.sendBlocking(it).right() }

    private fun parseEvent(rawEvent: Payload<IRawEvent>) = when (rawEvent.opcode()) {
        Opcode.HEARTBEAT -> HeartbeatEvent(rawEvent.unwrap()).right()
        Opcode.RECONNECT -> ReconnectEvent().right()
        Opcode.INVALID_SESSION -> InvalidSessionEvent(rawEvent.unwrapAsResponse()).right()
        Opcode.HELLO -> HelloEvent(rawEvent.unwrap()).right()
        Opcode.HEARTBEAT_ACK -> HeartbeatACKEvent().right()
        Opcode.DISPATCH -> parseDispatch(rawEvent)
        else -> ParseError.NonExistentOpcode.left()
    }

    private fun parseDispatch(rawEvent: Payload<IRawEvent>) = when (rawEvent.name) {
        "READY" -> ReadyEvent(rawEvent.unwrap()).right()
        "RESUMED" -> ResumedEvent().right()
        "CHANNEL_CREATE" -> ChannelCreateEvent(rawEvent.unwrapAsResponse()).right()
        "CHANNEL_UPDATE" -> ChannelUpdateEvent(rawEvent.unwrapAsResponse()).right()
        "CHANNEL_DELETE" -> ChannelDeleteEvent(rawEvent.unwrapAsResponse()).right()
        "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdateEvent(rawEvent.unwrap()).right()
        "GUILD_CREATE" -> GuildCreateEvent(rawEvent.unwrapAsResponse()).right()
        "GUILD_UPDATE" -> GuildUpdateEvent(rawEvent.unwrapAsResponse()).right()
        "GUILD_DELETE" -> GuildDeleteEvent(rawEvent.unwrapAsResponse()).right()
        "GUILD_BAN_ADD" -> BanEvent(rawEvent.unwrap()).right()
        "GUILD_BAN_REMOVE" -> UnbanEvent(rawEvent.unwrap()).right()
        "GUILD_EMOJIS_UPDATE" -> EmojisUpdateEvent(rawEvent.unwrap()).right()
        "GUILD_INTEGRATIONS_UPDATE" -> IntegrationsUpdateEvent(rawEvent.unwrap()).right()
        "GUILD_MEMBER_ADD" -> MemberJoinEvent(rawEvent.unwrapAsResponse()).right()
        "GUILD_MEMBER_REMOVE" -> MemberRemoveEvent(rawEvent.unwrap()).right()
        "GUILD_MEMBER_UPDATE" -> MemberUpdateEvent(rawEvent.unwrap()).right()
        "GUILD_MEMBER_CHUNK" -> MemberChunkEvent(rawEvent.unwrap()).right()
        "GUILD_ROLE_CREATE" -> RoleCreateEvent(rawEvent.unwrap()).right()
        "GUILD_ROLE_UPDATE" -> RoleUpdateEvent(rawEvent.unwrap()).right()
        "GUILD_ROLE_DELETE" -> RoleDeleteEvent(rawEvent.unwrap()).right()
        "MESSAGE_CREATE" -> MessageCreateEvent(rawEvent.unwrapAsResponse()).right()
        "MESSAGE_UPDATE" -> MessageUpdateEvent(rawEvent.unwrapAsResponse()).right()
        "MESSAGE_DELETE" -> MessageDeleteEvent(rawEvent.unwrap()).right()
        "MESSAGE_DELETE_BULK" -> MessageBulkDeleteEvent(rawEvent.unwrap()).right()
        "MESSAGE_REACTION_ADD" -> ReactionAddEvent(rawEvent.unwrap()).right()
        "MESSAGE_REACTION_REMOVE" -> ReactionRemoveEvent(rawEvent.unwrap()).right()
        "MESSAGE_REACTION_REMOVE_ALL" -> AllReactionsRemoveEvent(rawEvent.unwrap()).right()
        "PRESENCE_UPDATE" -> PresenceUpdateEvent(rawEvent.unwrap()).right()
        "PRESENCES_REPLACE" -> PresencesReplaceEvent().right()
        "TYPING_START" -> TypingEvent(rawEvent.unwrap()).right()
        "USER_UPDATE" -> UserUpdateEvent(rawEvent.unwrapAsResponse()).right()
        "VOICE_STATE_UPDATE" -> VoiceStateUpdateEvent(rawEvent.unwrapAsResponse()).right()
        "VOICE_SERVER_UPDATE" -> VoiceServerUpdateEvent(rawEvent.unwrap()).right()
        "WEBHOOKS_UPDATE" -> WebhooksUpdateEvent(rawEvent.unwrap()).right()
        else -> ParseError.NonExistentDispatch(rawEvent).left()
    }

    @ExperimentalCoroutinesApi
    @Suppress("UNCHECKED_CAST")
    override fun <E : IEvent> subscribeOn(type: Class<E>) = asyncF<E> { sink ->
        effect {
            val receiveChannel = channel.openSubscription()
            if (receiveChannel.isClosedForReceive)
                sink(IllegalStateException("Underlying channel is closed").left())

            for (event in receiveChannel) {
                event as? E ?: continue
                sink(event.right())
            }
        }
    }
}

package ru.tesserakt.diskordin.impl.core.client

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import ru.tesserakt.diskordin.core.client.EventDispatcher
import ru.tesserakt.diskordin.core.data.event.*
import ru.tesserakt.diskordin.core.data.event.channel.ChannelCreateEvent
import ru.tesserakt.diskordin.core.data.event.channel.ChannelDeleteEvent
import ru.tesserakt.diskordin.core.data.event.channel.ChannelPinsUpdateEvent
import ru.tesserakt.diskordin.core.data.event.channel.ChannelUpdateEvent
import ru.tesserakt.diskordin.core.data.event.guild.*
import ru.tesserakt.diskordin.core.data.event.lifecycle.*
import ru.tesserakt.diskordin.core.data.event.message.MessageBulkDeleteEvent
import ru.tesserakt.diskordin.core.data.event.message.MessageCreateEvent
import ru.tesserakt.diskordin.core.data.event.message.MessageDeleteEvent
import ru.tesserakt.diskordin.core.data.event.message.MessageUpdateEvent
import ru.tesserakt.diskordin.core.data.event.message.reaction.AllReactionsRemoveEvent
import ru.tesserakt.diskordin.core.data.event.message.reaction.ReactionAddEvent
import ru.tesserakt.diskordin.core.data.event.message.reaction.ReactionRemoveEvent
import ru.tesserakt.diskordin.gateway.Gateway
import ru.tesserakt.diskordin.gateway.GatewayAPI
import ru.tesserakt.diskordin.gateway.json.*
import ru.tesserakt.diskordin.gateway.json.commands.Identify
import ru.tesserakt.diskordin.gateway.json.commands.Resume
import ru.tesserakt.diskordin.util.receiveAsFlow

@ExperimentalCoroutinesApi
internal class EventDispatcherImpl(private val gateway: Gateway, private val api: GatewayAPI) : EventDispatcher() {
    override fun sendAnswer(payload: IGatewayCommand) = when (payload) {
        is Identify -> api.identify(payload.wrapWith(Opcode.IDENTIFY, gateway.lastSequenceId))
        is Heartbeat -> api.heartbeat(payload.wrapWith(Opcode.HEARTBEAT, gateway.lastSequenceId))
        is Resume -> api.resume(payload.wrapWith(Opcode.RESUME, gateway.lastSequenceId))
        else -> TODO()
    }

    private val channel = BroadcastChannel<IEvent>(Channel.CONFLATED)

    override suspend fun publish(rawEvent: Payload<IRawEvent>) = parseEvent(rawEvent)
        .let { channel.send(it) }

    private fun parseEvent(rawEvent: Payload<IRawEvent>) = when (rawEvent.opcode()) {
        Opcode.HEARTBEAT -> HeartbeatEvent(rawEvent.unwrap())
        Opcode.RECONNECT -> TODO()
        Opcode.INVALID_SESSION -> InvalidSessionEvent(rawEvent.unwrapAsResponse())
        Opcode.HELLO -> HelloEvent(rawEvent.unwrap())
        Opcode.HEARTBEAT_ACK -> HeartbeatACKEvent()
        Opcode.DISPATCH -> parseDispatch(rawEvent)
        else -> error("Only send")
    }

    private fun parseDispatch(rawEvent: Payload<IRawEvent>) = when (rawEvent.name) {
        "READY" -> ReadyEvent(rawEvent.unwrap())
        "RESUMED" -> ResumedEvent()
        "CHANNEL_CREATE" -> ChannelCreateEvent(rawEvent.unwrapAsResponse())
        "CHANNEL_UPDATE" -> ChannelUpdateEvent(rawEvent.unwrapAsResponse())
        "CHANNEL_DELETE" -> ChannelDeleteEvent(rawEvent.unwrapAsResponse())
        "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdateEvent(rawEvent.unwrap())
        "GUILD_CREATE" -> GuildCreateEvent(rawEvent.unwrapAsResponse())
        "GUILD_UPDATE" -> GuildUpdateEvent(rawEvent.unwrapAsResponse())
        "GUILD_DELETE" -> GuildDeleteEvent(rawEvent.unwrapAsResponse())
        "GUILD_BAN_ADD" -> BanEvent(rawEvent.unwrap())
        "GUILD_BAN_REMOVE" -> UnbanEvent(rawEvent.unwrap())
        "GUILD_EMOJIS_UPDATE" -> EmojisUpdateEvent(rawEvent.unwrap())
        "GUILD_INTEGRATIONS_UPDATE" -> IntegrationsUpdateEvent(rawEvent.unwrap())
        "GUILD_MEMBER_ADD" -> MemberJoinEvent(rawEvent.unwrap())
        "GUILD_MEMBER_REMOVE" -> MemberRemoveEvent(rawEvent.unwrap())
        "GUILD_MEMBER_UPDATE" -> MemberUpdateEvent(rawEvent.unwrap())
        "GUILD_MEMBER_CHUNK" -> MemberChunkEvent(rawEvent.unwrap())
        "GUILD_ROLE_CREATE" -> RoleCreateEvent(rawEvent.unwrap())
        "GUILD_ROLE_UPDATE" -> RoleUpdateEvent(rawEvent.unwrap())
        "GUILD_ROLE_DELETE" -> RoleDeleteEvent(rawEvent.unwrap())
        "MESSAGE_CREATE" -> MessageCreateEvent(rawEvent.unwrapAsResponse())
        "MESSAGE_UPDATE" -> MessageUpdateEvent(rawEvent.unwrapAsResponse())
        "MESSAGE_DELETE" -> MessageDeleteEvent(rawEvent.unwrap())
        "MESSAGE_DELETE_BULK" -> MessageBulkDeleteEvent(rawEvent.unwrap())
        "MESSAGE_REACTION_ADD" -> ReactionAddEvent(rawEvent.unwrap())
        "MESSAGE_REACTION_REMOVE" -> ReactionRemoveEvent(rawEvent.unwrap())
        "MESSAGE_REACTION_REMOVE_ALL" -> AllReactionsRemoveEvent(rawEvent.unwrap())
        "PRESENCE_UPDATE" -> PresenceUpdateEvent(rawEvent.unwrap())
        "PRESENCE_REPLACE" -> PresenceReplaceEvent(rawEvent.unwrap())
        "TYPING_START" -> TypingEvent(rawEvent.unwrap())
        "USER_UPDATE" -> UserUpdateEvent(rawEvent.unwrapAsResponse())
        "VOICE_STATE_UPDATE" -> VoiceStateUpdateEvent(rawEvent.unwrapAsResponse())
        "VOICE_SERVER_UPDATE" -> VoiceServerUpdateEvent(rawEvent.unwrap())
        "WEBHOOKS_UPDATE" -> WebhooksUpdateEvent(rawEvent.unwrap())
        else -> throw NoSuchElementException("No such event name or opcode: ${rawEvent.opcode}, ${rawEvent.name}")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <E : IEvent> subscribeOn(type: Class<E>) =
        channel.openSubscription()
            .receiveAsFlow()
            .filter { type.isInstance(it) }
            .map { it as E }
}

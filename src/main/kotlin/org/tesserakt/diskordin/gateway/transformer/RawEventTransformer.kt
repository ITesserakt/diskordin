package org.tesserakt.diskordin.gateway.transformer

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

object RawEventTransformer :
    Transformer<Payload<IRawEvent>, IEvent> {
    override fun transform(context: Payload<IRawEvent>): IEvent = when (context.opcode()) {
        Opcode.HEARTBEAT -> HeartbeatEvent(context.unwrap())
        Opcode.RECONNECT -> ReconnectEvent()
        Opcode.INVALID_SESSION -> InvalidSessionEvent(context.unwrapAsResponse())
        Opcode.HELLO -> HelloEvent(context.unwrap())
        Opcode.HEARTBEAT_ACK -> HeartbeatACKEvent()
        Opcode.DISPATCH -> parseDispatch(
            context
        )
        else -> throw IllegalStateException("Should never raises")
    }

    private fun parseDispatch(context: Payload<IRawEvent>) = when (context.name) {
        "READY" -> ReadyEvent(context.unwrap())
        "RESUMED" -> ResumedEvent()
        "CHANNEL_CREATE" -> ChannelCreateEvent(context.unwrapAsResponse())
        "CHANNEL_UPDATE" -> ChannelUpdateEvent(context.unwrapAsResponse())
        "CHANNEL_DELETE" -> ChannelDeleteEvent(context.unwrapAsResponse())
        "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdateEvent(context.unwrap())
        "GUILD_CREATE" -> GuildCreateEvent(context.unwrapAsResponse())
        "GUILD_UPDATE" -> GuildUpdateEvent(context.unwrapAsResponse())
        "GUILD_DELETE" -> GuildDeleteEvent(context.unwrapAsResponse())
        "GUILD_BAN_ADD" -> BanEvent(context.unwrap())
        "GUILD_BAN_REMOVE" -> UnbanEvent(context.unwrap())
        "GUILD_EMOJIS_UPDATE" -> EmojisUpdateEvent(context.unwrap())
        "GUILD_INTEGRATIONS_UPDATE" -> IntegrationsUpdateEvent(context.unwrap())
        "GUILD_MEMBER_ADD" -> MemberJoinEvent(context.unwrapAsResponse())
        "GUILD_MEMBER_REMOVE" -> MemberRemoveEvent(context.unwrap())
        "GUILD_MEMBER_UPDATE" -> MemberUpdateEvent(context.unwrap())
        "GUILD_MEMBER_CHUNK" -> MemberChunkEvent(context.unwrap())
        "GUILD_ROLE_CREATE" -> RoleCreateEvent(context.unwrap())
        "GUILD_ROLE_UPDATE" -> RoleUpdateEvent(context.unwrap())
        "GUILD_ROLE_DELETE" -> RoleDeleteEvent(context.unwrap())
        "MESSAGE_CREATE" -> MessageCreateEvent(context.unwrapAsResponse())
        "MESSAGE_UPDATE" -> MessageUpdateEvent(context.unwrapAsResponse())
        "MESSAGE_DELETE" -> MessageDeleteEvent(context.unwrap())
        "MESSAGE_DELETE_BULK" -> MessageBulkDeleteEvent(context.unwrap())
        "MESSAGE_REACTION_ADD" -> ReactionAddEvent(context.unwrap())
        "MESSAGE_REACTION_REMOVE" -> ReactionRemoveEvent(context.unwrap())
        "MESSAGE_REACTION_REMOVE_ALL" -> AllReactionsRemoveEvent(context.unwrap())
        "PRESENCE_UPDATE" -> PresenceUpdateEvent(context.unwrap())
        "PRESENCES_REPLACE" -> PresencesReplaceEvent()
        "TYPING_START" -> TypingEvent(context.unwrap())
        "USER_UPDATE" -> UserUpdateEvent(context.unwrapAsResponse())
        "VOICE_STATE_UPDATE" -> VoiceStateUpdateEvent(context.unwrapAsResponse())
        "VOICE_SERVER_UPDATE" -> VoiceServerUpdateEvent(context.unwrap())
        "WEBHOOKS_UPDATE" -> WebhooksUpdateEvent(context.unwrap())
        else -> throw IllegalStateException("Should never raises")
    }
}
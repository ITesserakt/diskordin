package org.tesserakt.diskordin.core.data

import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.gateway.shard.Intents.*
import org.tesserakt.diskordin.util.enums.ValuedEnum
import kotlin.reflect.KClass

internal class EntitySifter(intents: ValuedEnum<Intents, Short>, private val isEnabled: Boolean) {
    private val denied = mutableSetOf<KClass<out IEntity>>()

    init {
        if (Guilds !in intents) denied += listOf(IRole::class, IMessage::class, IGuild::class, IGuildChannel::class)
        if (GuildMembers !in intents) denied += listOf(IMember::class, IUser::class)
        if (DirectMessages !in intents) denied += listOf(IChannel::class, IMessage::class)
        if (GuildBans !in intents || GuildPresences !in intents) denied += IUser::class
        if (GuildEmojis !in intents) denied += ICustomEmoji::class
        if (GuildMessages !in intents) denied += IMessage::class
        if (GuildMessageReactions !in intents) denied += ICustomEmoji::class
        if (GuildIntegrations !in intents
            || GuildWebhooks !in intents
            || GuildInvites !in intents
            || GuildVoiceStates !in intents
            || GuildMessageTyping !in intents
            || DirectMessageReactions !in intents
            || DirectMessageTyping !in intents
            || GuildPresences !in intents
        ) denied += emptySet()
    }

    @Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
    fun <T : Any> isAllowed(item: T) =
        item !is IEntity || denied.contains(item::class as KClass<out IEntity>) && isEnabled
}

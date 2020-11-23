package org.tesserakt.diskordin.core.data

import org.tesserakt.diskordin.core.client.BootstrapContext
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.gateway.shard.Intents
import org.tesserakt.diskordin.gateway.shard.Intents.*
import org.tesserakt.diskordin.util.enums.ValuedEnum
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class EntityCache internal constructor(
    cacheSifter: EntitySifter,
    private val initial: MutableMap<Snowflake, IEntity> = ConcurrentHashMap()
) : MutableMap<Snowflake, IEntity> by initial, BootstrapContext.ExtensionContext {
    companion object : BootstrapContext.PersistentExtension<EntityCache>

    private val deniedEntities = cacheSifter.sift()

    fun disabledFeatures() = deniedEntities.mapNotNull { it.simpleName }

    override fun put(key: Snowflake, value: IEntity) =
        if (value::class in deniedEntities || deniedEntities.any { it.java.isAssignableFrom(value::class.java) }) null
        else initial.put(key, value)

    override fun toString(): String =
        """EntityCache {disabled = ${disabledFeatures()}} with backend ${initial::class.simpleName}
           |     Holds $size elements with types ${map { it.value::class.simpleName }.toSet()} 
        """.trimMargin()
}

internal class EntitySifter(intents: ValuedEnum<Intents, Short>) {
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

    fun sift(): Set<KClass<out IEntity>> = denied
}

package org.tesserakt.diskordin.core.cache

import arrow.core.Either
import arrow.core.filterMap
import arrow.core.left
import arrow.core.right
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild

typealias SnowflakeMap<V> = Map<Snowflake, V>

interface CacheSnapshot {
    val privateChannels: SnowflakeMap<IPrivateChannel>
    val groupChannels: SnowflakeMap<IGroupPrivateChannel>
    val unavailableGuilds: SnowflakeMap<UnavailableGuild>
    val guilds: SnowflakeMap<IGuild>
    val messages: SnowflakeMap<IMessage>
    val lastTypes: SnowflakeMap<SnowflakeMap<Instant>>
    val users: SnowflakeMap<IUser>
    val bans: SnowflakeMap<SnowflakeMap<IBan>>

    fun getPrivateChannel(id: Snowflake): IPrivateChannel? = privateChannels[id]
    fun getUserPrivateChannel(userId: Snowflake): IPrivateChannel? =
        (privateChannels + groupChannels).values.firstOrNull { it.owner.id == userId }

    fun getGroupPrivateChannel(id: Snowflake): IGroupPrivateChannel? = groupChannels[id]
    fun getGuild(id: Snowflake): IGuild? = guilds[id]
    fun getAnyGuild(id: Snowflake): Either<UnavailableGuild, IGuild>? =
        guilds[id]?.right() ?: unavailableGuilds[id]?.left()

    fun getMessage(id: Snowflake): IMessage? = messages[id]
    fun getChannelMessages(channelId: Snowflake): SnowflakeMap<IMessage> =
        messages.filterValues { it.channel.id == channelId }

    fun getMessage(channelId: Snowflake, messageId: Snowflake) =
        getChannelMessages(channelId)[messageId]

    fun getGuildChannel(guildId: Snowflake, channelId: Snowflake): IGuildChannel? =
        guilds[guildId]?.cachedChannels?.find { it.id == channelId }

    fun getGuildChannel(id: Snowflake) = guilds.firstMap { g -> g.cachedChannels.find { it.id == id } }

    fun getChannel(id: Snowflake): IChannel? =
        getPrivateChannel(id) ?: getGroupPrivateChannel(id) ?: getGuildChannel(id)

    fun getTextChannel(id: Snowflake): ITextChannel? =
        getChannel(id)?.let { it as? ITextChannel }

    fun getRole(id: Snowflake): IRole? = guilds.firstMap { g -> g.roles.find { it.id == id } }

    fun getRole(guildId: Snowflake, roleId: Snowflake): IRole? = guilds[guildId]?.roles?.find { it.id == roleId }
    fun getUser(id: Snowflake): IUser? = users[id]
    fun getBans(guildId: Snowflake): SnowflakeMap<IBan> = bans[guildId].orEmpty()
    fun getBan(guildId: Snowflake, userId: Snowflake) = getBans(guildId)[userId]
    fun getMember(id: Snowflake) = guilds.firstMap { g -> g.members.find { it.id == id } }
    fun getMember(guildId: Snowflake, memberId: Snowflake) = guilds[guildId]?.members?.find { it.id == memberId }
}

fun <A, B> List<A>.firstMap(f: (A) -> B?) = mapNotNull(f).firstOrNull()
fun <A, B> Set<A>.firstMap(f: (A) -> B?) = mapNotNull(f).firstOrNull()
fun <K, V, U> Map<K, V>.firstMap(f: (V) -> U?) = filterMap(f).values.firstOrNull()
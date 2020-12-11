package org.tesserakt.diskordin.core.cache

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.map.foldable.find
import arrow.core.extensions.mapk.foldable.foldable
import arrow.core.extensions.mapk.functorFilter.functorFilter
import arrow.typeclasses.Foldable
import arrow.typeclasses.FunctorFilter
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.id
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
        (privateChannels + groupChannels).find { it.owner.id == userId }.orNull()

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
        guilds[guildId]?.channels?.find { it.id == channelId }

    fun getGuildChannel(id: Snowflake) = guilds.firstMap { g -> g.channels.find { it.id == id } }

    fun getChannel(id: Snowflake): IChannel? =
        getPrivateChannel(id) ?: getGroupPrivateChannel(id) ?: getGuildChannel(id)

    fun getTextChannel(id: Snowflake): ITextChannel? =
        getChannel(id).toOption().mapNotNull { it as? ITextChannel }.orNull()

    fun getRole(id: Snowflake): IRole? = guilds.firstMap { g -> g.roles.find { it.id == id } }

    fun getRole(guildId: Snowflake, roleId: Snowflake): IRole? = guilds[guildId]?.roles?.find { it.id == roleId }
    fun getUser(id: Snowflake): IUser? = users[id]
    fun getBans(guildId: Snowflake): SnowflakeMap<IBan> = bans[guildId].orEmpty()
    fun getBan(guildId: Snowflake, userId: Snowflake) = getBans(guildId)[userId]
    fun getMember(id: Snowflake) = guilds.firstMap { g -> g.members.find { it.id == id } }
    fun getMember(guildId: Snowflake, memberId: Snowflake) = guilds[guildId]?.members?.find { it.id == memberId }
}

fun <F, A, B> Kind<F, A>.firstMap(FF: FunctorFilter<F>, FL: Foldable<F>, f: (A) -> B?) = FF.run {
    FL.run { filterMap { f(it).toOption() }.firstOrNone().orNull() }
}

fun <K, V, U> Map<K, V>.firstMap(f: (V) -> U?) = k().firstMap(MapK.functorFilter(), MapK.foldable(), f)
package org.tesserakt.diskordin.core.cache

import arrow.core.extensions.map.functor.map
import arrow.fx.coroutines.ForkConnected
import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import kotlinx.coroutines.Dispatchers
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.`object`.IBan
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild
import org.tesserakt.diskordin.impl.core.entity.*
import org.tesserakt.diskordin.impl.core.entity.`object`.Ban
import org.tesserakt.diskordin.util.fromJson
import org.tesserakt.diskordin.util.toJson
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.time.Instant

private const val impossibleDelimiter = "\n~--+-=$#%~***~%#$=-+--~\n"

private fun SnowflakeMap<IPrivateChannel>.privateChannelsConverter() =
    map { listOf("PrivateChannel", (it as PrivateChannel).raw.toJson()) }

private fun SnowflakeMap<IGroupPrivateChannel>.groupChannelsConverter() =
    map { listOf("GroupChannel", (it as GroupPrivateChannel).raw.toJson()) }

private fun SnowflakeMap<IGuild>.guildConverter() = map { guild ->
    when (guild) {
        is Guild -> Guild::class.simpleName to guild.raw.toJson()
        is PartialGuild -> PartialGuild::class.simpleName to guild.raw.toJson()
        else -> throw IllegalArgumentException("Given `${guild::class.simpleName}`, but no guild found for this one")
    }.let { listOf(it.first, it.second) }
}

private fun SnowflakeMap<IMessage>.messageConverter() =
    map { arrayOf("Message", (it as Message).raw.toJson()) }

private fun SnowflakeMap<IUser>.userConverter() = map { user ->
    when (user) {
        is Self -> Self::class.simpleName to user.raw.toJson()
        is IdUser -> IdUser::class.simpleName to user.raw.toJson()
        is MessageUser -> MessageUser::class.simpleName to user.raw.toJson()
        else -> throw IllegalArgumentException("Given `${user::class.simpleName}`, but no user found for this one")
    }.let { listOf(it.first, it.second) }
}

private fun SnowflakeMap<SnowflakeMap<IBan>>.banConverter() =
    map { listOf("Bans", it.map { b -> (b as Ban).raw.toJson() }.toJson()) }

class FileCacheSnapshot private constructor(private val text: Sequence<String>) : CacheSnapshot {
    private enum class Patterns(val value: String) {
        PrivateChannels("-=Private channels=-"),
        GroupChannels("-=Group channels=-"),
        UnavailableGuilds("-=Unavailable guilds=-"),
        Guilds("-=Guilds=-"),
        Messages("-=Messages=-"),
        LastTypes("-=Last types=-"),
        Users("-=Users=-"),
        Bans("-=Bans=-");

        val length = value.length
    }

    private fun <T> getFromText(title: Patterns, unwrap: (String, String) -> T) = lazy {
        text.find { it.startsWith(title.value) }?.drop(title.length)
            ?.fromJson<SnowflakeMap<List<String>>>()
            .orEmpty().map { unwrap(it[0], it[1]) }
    }

    override val privateChannels by getFromText(Patterns.PrivateChannels) { _, it ->
        it.fromJson<ChannelResponse<IPrivateChannel>>().unwrap()
    }
    override val groupChannels by getFromText(Patterns.GroupChannels) { _, it ->
        it.fromJson<ChannelResponse<IGroupPrivateChannel>>().unwrap()
    }
    override val unavailableGuilds by getFromText(Patterns.UnavailableGuilds) { _, it ->
        it.fromJson<UnavailableGuild>()
    }
    override val guilds by getFromText(Patterns.Guilds) { type, it ->
        when (type) {
            Guild::class.simpleName -> it.fromJson<GuildResponse>().unwrap()
            PartialGuild::class.simpleName -> it.fromJson<UserGuildResponse>().unwrap()
            else -> throw IllegalArgumentException("Given `$type`, but no guild found for this one")
        }
    }
    override val messages by getFromText(Patterns.Messages) { _, it ->
        it.fromJson<MessageResponse>().unwrap()
    }
    override val lastTypes by getFromText(Patterns.LastTypes) { _, it ->
        it.fromJson<SnowflakeMap<Instant>>()
    }
    override val users by getFromText(Patterns.Users) { type, it ->
        when (type) {
            Self::class.simpleName -> it.fromJson<UserResponse<IUser>>().unwrap()
            IdUser::class.simpleName -> it.fromJson<IDUserResponse>().unwrap()
            MessageUser::class.simpleName -> it.fromJson<MessageUserResponse>().unwrap()
            else -> throw IllegalArgumentException("Given `$type`, but no user found for this one")
        }
    }
    override val bans by getFromText(Patterns.Bans) { _, it ->
        it.fromJson<SnowflakeMap<BanResponse>>().map { it.unwrap() }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun writeTo(writer: BufferedWriter) = resource { writer } release {
        ForkConnected(Dispatchers.IO) { it.close() }.join()
    } use { writer.write(text.joinToString(impossibleDelimiter)) }

    fun loadToMemory() = MemoryCacheSnapshot(
        privateChannels, groupChannels, unavailableGuilds, guilds, messages, lastTypes, users, bans
    )

    companion object {
        suspend operator fun invoke(file: File) = FileCacheSnapshot(file.bufferedReader())

        suspend operator fun invoke(reader: BufferedReader) = resource { reader }.release {
            ForkConnected(Dispatchers.IO) { reader.close() }.join()
        }.map { it.readText().splitToSequence(impossibleDelimiter) }.map(::FileCacheSnapshot)

        suspend fun fromMemorySnapshot(snapshot: MemoryCacheSnapshot): FileCacheSnapshot {
            val text = sequence {
                yield(Patterns.PrivateChannels.value + snapshot.privateChannels.privateChannelsConverter().toJson())
                yield(Patterns.GroupChannels.value + snapshot.groupChannels.groupChannelsConverter().toJson())
                yield(Patterns.UnavailableGuilds.value + snapshot.unavailableGuilds.toJson())
                yield(Patterns.Guilds.value + snapshot.guilds.guildConverter().toJson())
                yield(Patterns.Messages.value + snapshot.messages.messageConverter().toJson())
                yield(Patterns.LastTypes.value + snapshot.lastTypes.toJson())
                yield(Patterns.Users.value + snapshot.users.userConverter().toJson())
                yield(Patterns.Bans.value + snapshot.bans.banConverter().toJson())
            }

            return FileCacheSnapshot(text)
        }
    }
}
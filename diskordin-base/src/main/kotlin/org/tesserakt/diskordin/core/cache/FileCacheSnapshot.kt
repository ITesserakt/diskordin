package org.tesserakt.diskordin.core.cache

import arrow.fx.coroutines.release
import arrow.fx.coroutines.resource
import com.google.gson.JsonElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import org.tesserakt.diskordin.core.data.json.response.*
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.gateway.json.events.UnavailableGuild
import org.tesserakt.diskordin.impl.core.entity.*
import org.tesserakt.diskordin.impl.core.entity.`object`.Ban
import org.tesserakt.diskordin.util.fromJson
import org.tesserakt.diskordin.util.gson
import org.tesserakt.diskordin.util.toJson
import org.tesserakt.diskordin.util.toJsonTree
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File

private fun SnowflakeMap<IGuild>.guildConverter() = mapValues { (_, a) ->
    when (a) {
        is Guild -> TypedObject<IGuild, GuildResponse>(a.raw)
        is PartialGuild -> TypedObject<IGuild, UserGuildResponse>(a.raw)
        else -> throw IllegalArgumentException("Given `${a::class.simpleName}`, but no guild found for this one")
    }
}

private fun SnowflakeMap<IUser>.userConverter() = mapValues { (_, a) ->
    when (a) {
        is Self -> TypedObject<IUser, UserResponse<IUser>>(a.raw as UserResponse<IUser>)
        is IdUser -> TypedObject<IUser, IDUserResponse>(a.raw)
        is MessageUser -> TypedObject<IUser, MessageUserResponse>(a.raw)
        else -> throw IllegalArgumentException("Given `${a::class.simpleName}`, but no user found for this one")
    }
}

@Suppress("DataClassPrivateConstructor")
private data class TypedObject<O : IEntity> private constructor(val type: String, val response: JsonElement) {
    @Suppress("UNCHECKED_CAST")
    fun <P : DiscordResponse<O, UnwrapContext.EmptyContext>> parse() =
        (gson.fromJson(response, Class.forName(type)) as P).unwrap()

    companion object {
        inline operator fun <O : IEntity, reified R : DiscordResponse<O, UnwrapContext.EmptyContext>> invoke(value: R) =
            TypedObject<O>(R::class.simpleName!!, value.toJsonTree())
    }
}

private data class CacheRepresentation(
    val privateChannels: SnowflakeMap<ChannelResponse<IPrivateChannel>>,
    val groupPrivateChannels: SnowflakeMap<ChannelResponse<IGroupPrivateChannel>>,
    val unavailableGuilds: SnowflakeMap<UnavailableGuild>,
    val guilds: SnowflakeMap<TypedObject<IGuild>>,
    val messages: SnowflakeMap<MessageResponse>,
    val lastTypes: SnowflakeMap<SnowflakeMap<Instant>>,
    val users: SnowflakeMap<TypedObject<IUser>>,
    val bans: SnowflakeMap<SnowflakeMap<BanResponse>>
)

class FileCacheSnapshot private constructor(private val representation: CacheRepresentation) : CacheSnapshot {
    override val privateChannels by lazy { representation.privateChannels.mapValues { (_, a) -> a.unwrap() } }
    override val groupChannels by lazy { representation.groupPrivateChannels.mapValues { (_, a) -> a.unwrap() } }
    override val unavailableGuilds by lazy { representation.unavailableGuilds }
    override val guilds by lazy {
        representation.guilds.mapValues { (_, a) ->
            when (a.type) {
                GuildResponse::class.simpleName -> a.parse<GuildResponse>()
                UserGuildResponse::class.simpleName -> a.parse<UserGuildResponse>()
                else -> throw IllegalArgumentException("Given `$a.type`, but no guild found for this one")
            }
        }
    }
    override val messages by lazy { representation.messages.mapValues { (_, a) -> a.unwrap() } }
    override val lastTypes: SnowflakeMap<SnowflakeMap<Instant>> by lazy { representation.lastTypes }
    override val users by lazy {
        representation.users.mapValues { (_, a) ->
            when (a.type) {
                UserResponse::class.simpleName -> a.parse<UserResponse<ISelf>>()
                IDUserResponse::class.simpleName -> a.parse<IDUserResponse>()
                MessageUserResponse::class.simpleName -> a.parse<MessageUserResponse>()
                else -> throw IllegalArgumentException("Given `$a.type`, but no user found for this one")
            }
        }
    }
    override val bans by lazy {
        representation.bans.mapValues { (_, a) -> a.mapValues { (_, a) -> a.unwrap() } }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun writeTo(writer: BufferedWriter) = resource { writer } release {
        withContext(Dispatchers.IO) { it.close() }
    } use { writer.write(representation.toJson()) }

    fun loadToMemory() = MemoryCacheSnapshot(
        privateChannels, groupChannels, unavailableGuilds, guilds, messages, lastTypes, users, bans
    )

    companion object {
        suspend operator fun invoke(file: File) = FileCacheSnapshot(file.bufferedReader())

        @Suppress("BlockingMethodInNonBlockingContext")
        suspend operator fun invoke(reader: BufferedReader) = resource { reader }.release {
            withContext(Dispatchers.IO) { it.close() }
        }.map { it.readText().fromJson<CacheRepresentation>() }.map(::FileCacheSnapshot)

        fun fromSnapshot(snapshot: CacheSnapshot): FileCacheSnapshot {
            if (snapshot is FileCacheSnapshot) return snapshot

            val representation = CacheRepresentation(
                snapshot.privateChannels.mapValues { (_, a) -> (a as PrivateChannel).raw },
                snapshot.groupChannels.mapValues { (_, a) -> (a as GroupPrivateChannel).raw },
                snapshot.unavailableGuilds,
                snapshot.guilds.guildConverter(),
                snapshot.messages.mapValues { (_, a) -> (a as Message).raw },
                snapshot.lastTypes,
                snapshot.users.userConverter(),
                snapshot.bans.mapValues { (_, a) -> a.mapValues { (_, a) -> (a as Ban).raw } }
            )

            return FileCacheSnapshot(representation)
        }
    }
}
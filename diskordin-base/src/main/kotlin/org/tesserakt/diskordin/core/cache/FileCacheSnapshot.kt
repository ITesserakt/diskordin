package org.tesserakt.diskordin.core.cache

import arrow.core.extensions.map.functor.map
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

private fun SnowflakeMap<IGuild>.guildConverter() = map { guild ->
    when (guild) {
        is Guild -> TypedObject(guild.raw)
        is PartialGuild -> TypedObject(guild.raw)
        else -> throw IllegalArgumentException("Given `${guild::class.simpleName}`, but no guild found for this one")
    }
}

private fun SnowflakeMap<IUser>.userConverter() = map { user ->
    when (user) {
        is Self -> TypedObject(user.raw as UserResponse<IUser>)
        is IdUser -> TypedObject(user.raw)
        is MessageUser -> TypedObject(user.raw)
        else -> throw IllegalArgumentException("Given `${user::class.simpleName}`, but no user found for this one")
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
    override val privateChannels by lazy { representation.privateChannels.map { it.unwrap() } }
    override val groupChannels by lazy { representation.groupPrivateChannels.map { it.unwrap() } }
    override val unavailableGuilds by lazy { representation.unavailableGuilds }
    override val guilds by lazy {
        representation.guilds.map {
            when (it.type) {
                GuildResponse::class.simpleName -> it.parse<GuildResponse>()
                UserGuildResponse::class.simpleName -> it.parse<UserGuildResponse>()
                else -> throw IllegalArgumentException("Given `$it.type`, but no guild found for this one")
            }
        }
    }
    override val messages by lazy { representation.messages.map { it.unwrap() } }
    override val lastTypes: SnowflakeMap<SnowflakeMap<Instant>> by lazy { representation.lastTypes }
    override val users by lazy {
        representation.users.map {
            when (it.type) {
                UserResponse::class.simpleName -> it.parse<UserResponse<ISelf>>()
                IDUserResponse::class.simpleName -> it.parse<IDUserResponse>()
                MessageUserResponse::class.simpleName -> it.parse<MessageUserResponse>()
                else -> throw IllegalArgumentException("Given `$it.type`, but no user found for this one")
            }
        }
    }
    override val bans by lazy {
        representation.bans.map { it.map(BanResponse::unwrap) }
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
                snapshot.privateChannels.map { (it as PrivateChannel).raw },
                snapshot.groupChannels.map { (it as GroupPrivateChannel).raw },
                snapshot.unavailableGuilds,
                snapshot.guilds.guildConverter(),
                snapshot.messages.map { (it as Message).raw },
                snapshot.lastTypes,
                snapshot.users.userConverter(),
                snapshot.bans.map { inner -> inner.map { (it as Ban).raw } }
            )

            return FileCacheSnapshot(representation)
        }
    }
}
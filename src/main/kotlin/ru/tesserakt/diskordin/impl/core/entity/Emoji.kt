package ru.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.some
import arrow.fx.fix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.rest.call

open class Emoji(raw: EmojiResponse<IEmoji>) : IEmoji {
    final override val name: String = raw.name

    override fun toString(): String {
        return "Emoji(name='$name')"
    }
}

class CustomEmoji constructor(
    raw: EmojiResponse<ICustomEmoji>,
    guildId: Snowflake
) : Emoji(raw), ICustomEmoji {
    override suspend fun edit(builder: EmojiEditBuilder.() -> Unit) = rest.call(guild.id.some(), Id.functor()) {
        emojiService.editGuildEmoji(guild.id, id, builder.build())
    }.fix().suspended().extract()

    override val guild: Identified<IGuild> =
        Identified(guildId) {
            client.findGuild(it) ?: throw NotCustomEmojiException()
        }

    override val roles: Flow<IRole> = flow {
        raw.roles?.map(Long::asSnowflake)
            ?.map {
                client.findGuild(guildId)?.getRole(it) ?: throw NotCustomEmojiException()
            }?.forEach { emit(it) } ?: throw NotCustomEmojiException()
    }

    override val creator: Identified<IUser> =
        Identified(
            raw.user?.id ?: throw NotCustomEmojiException()
        ) { User(raw.user) }

    override val requireColons: Boolean = raw.require_colons ?: throw NotCustomEmojiException()

    override val isManaged: Boolean = raw.managed ?: throw NotCustomEmojiException()

    override val isAnimated: Boolean = raw.animated ?: throw NotCustomEmojiException()

    override val id: Snowflake = raw.id ?: throw NotCustomEmojiException()

    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:${id.asString()}>"

    override suspend fun delete(reason: String?) = rest.effect {
        emojiService.deleteGuildEmoji(guild.id, id)
    }.fix().suspended()

    override fun toString(): String {
        return "CustomEmoji(guild=$guild, roles=$roles, creator=$creator, requireColons=$requireColons, isManaged=$isManaged, isAnimated=$isAnimated, id=$id, mention='$mention') ${super.toString()}"
    }
}

class NotCustomEmojiException : IllegalArgumentException("Not a custom emoji")
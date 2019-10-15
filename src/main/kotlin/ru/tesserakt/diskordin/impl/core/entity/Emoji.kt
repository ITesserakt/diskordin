package ru.tesserakt.diskordin.impl.core.entity


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import ru.tesserakt.diskordin.core.entity.builder.build
import ru.tesserakt.diskordin.util.Identified

open class Emoji(raw: EmojiResponse<IEmoji>) : IEmoji {
    override val name: String = raw.name
}

class CustomEmoji constructor(
    raw: EmojiResponse<ICustomEmoji>,
    guildId: Snowflake
) : Emoji(raw), ICustomEmoji {
    override suspend fun edit(builder: EmojiEditBuilder.() -> Unit): ICustomEmoji =
        emojiService.editGuildEmoji(guild.id, id, builder.build()).unwrap()

    override val guild: Identified<IGuild> = Identified(guildId) {
        client.findGuild(it) ?: throw NotCustomEmojiException()
    }

    override val roles: Flow<IRole> = flow {
        raw.roles?.map(Long::asSnowflake)
            ?.map {
                client.findGuild(guildId)?.getRole(it) ?: throw NotCustomEmojiException()
            }
            ?.forEach { emit(it) } ?: throw NotCustomEmojiException()
    }

    override val creator: Identified<IUser> = Identified(
        raw.user?.id?.asSnowflake() ?: throw NotCustomEmojiException()
    ) { User(raw.user) }

    override val requireColons: Boolean = raw.require_colons ?: throw NotCustomEmojiException()

    override val isManaged: Boolean = raw.managed ?: throw NotCustomEmojiException()

    override val isAnimated: Boolean = raw.animated ?: throw NotCustomEmojiException()

    override val id: Snowflake = raw.id?.asSnowflake() ?: throw NotCustomEmojiException()

    override val name: String = raw.name

    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:$id>"

    override suspend fun delete(reason: String?) = emojiService.deleteGuildEmoji(guild.id, id)
}

class NotCustomEmojiException : IllegalArgumentException("Not a custom emoji")
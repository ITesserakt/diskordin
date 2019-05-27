package ru.tesserakt.diskordin.impl.core.entity

import arrow.core.getOrElse
import arrow.core.handleError
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.rest.service.EmojiService
import ru.tesserakt.diskordin.util.Identified

class Emoji(raw: EmojiResponse, override val kodein: Kodein) : IEmoji {
    override val client: IDiscordClient by instance()

    override val name: String = raw.name
}

class CustomEmoji constructor(
    raw: EmojiResponse,
    guildId: Snowflake,
    override val kodein: Kodein
) : ICustomEmoji {
    override val guild: Identified<IGuild> = Identified(guildId) {
        client.findGuild(it).getOrElse { throw NotCustomEmojiException() }
    }

    @FlowPreview
    override val roles: Flow<IRole> = flow {
        raw.roles?.map { it.asSnowflake() }
            ?.map {
                client.findGuild(guildId)
                    .flatMap { guild -> guild.findRole(it) }
                    .getOrElse { throw NotCustomEmojiException() }
            }
            ?.forEach { emit(it) } ?: throw NotCustomEmojiException()
    }


    override val creator: Identified<IUser> = Identified(
        raw.user?.id?.asSnowflake() ?: throw NotCustomEmojiException()
    ) {
        User(raw.user, kodein)
    }

    override val requireColons: Boolean = raw.require_colons ?: throw NotCustomEmojiException()

    override val isManaged: Boolean = raw.managed ?: throw NotCustomEmojiException()

    override val isAnimated: Boolean = raw.animated ?: throw NotCustomEmojiException()


    override val id: Snowflake = raw.id?.asSnowflake() ?: throw NotCustomEmojiException()

    override val client: IDiscordClient by instance()

    override val name: String = raw.name


    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:$id>"


    override suspend fun delete(reason: String?) {
        EmojiService.General
            .deleteEmoji(guild.state.asLong(), id.asLong())
            .handleError { throw it }
    }
}

class NotCustomEmojiException : IllegalArgumentException("Not a custom emoji")
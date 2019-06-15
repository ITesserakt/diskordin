package ru.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.entity.*
import ru.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import ru.tesserakt.diskordin.impl.core.rest.resource.EmojiResource
import ru.tesserakt.diskordin.impl.core.service.EmojiService
import ru.tesserakt.diskordin.util.Identified

open class Emoji(raw: EmojiResponse, final override val kodein: Kodein = Diskordin.kodein) : IEmoji {
    override val client: IDiscordClient by instance()

    override val name: String = raw.name
}

class CustomEmoji constructor(
    raw: EmojiResponse,
    guildId: Snowflake
) : Emoji(raw), ICustomEmoji {
    override suspend fun edit(builder: EmojiEditBuilder.() -> Unit): ICustomEmoji =
        EmojiService.editEmoji(guild.state, id, builder)

    override val guild: Identified<IGuild> = Identified(guildId) {
        client.coroutineScope.async {
            client.findGuild(it) ?: throw NotCustomEmojiException()
        }
    }

    @ExperimentalCoroutinesApi
    override val roles: Flow<IRole> = flow {
        raw.roles?.map(Long::asSnowflake)
            ?.map {
                client.findGuild(guildId)?.findRole(it) ?: throw NotCustomEmojiException()
            }
            ?.forEach { emit(it) } ?: throw NotCustomEmojiException()
    }

    override val creator: Identified<IUser> = Identified(
        raw.user?.id?.asSnowflake() ?: throw NotCustomEmojiException()
    ) {
        client.coroutineScope.async {
            User(raw.user, kodein)
        }
    }

    override val requireColons: Boolean = raw.require_colons ?: throw NotCustomEmojiException()

    override val isManaged: Boolean = raw.managed ?: throw NotCustomEmojiException()

    override val isAnimated: Boolean = raw.animated ?: throw NotCustomEmojiException()


    override val id: Snowflake = raw.id?.asSnowflake() ?: throw NotCustomEmojiException()

    override val client: IDiscordClient by instance()

    override val name: String = raw.name


    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:$id>"


    override suspend fun delete(reason: String?) = EmojiResource.General
        .deleteEmoji(guild.state.asLong(), id.asLong())
}

class NotCustomEmojiException : IllegalArgumentException("Not a custom emoji")
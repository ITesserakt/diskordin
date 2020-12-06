package org.tesserakt.diskordin.impl.core.entity


import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.some
import arrow.fx.coroutines.stream.Chunk
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.filterNotNull
import org.tesserakt.diskordin.core.data.*
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.rest.call

internal open class Emoji(raw: EmojiResponse<IEmoji>) : IEmoji {
    final override val name: String = raw.name

    override fun toString(): String {
        return "Emoji(name='$name')"
    }
}

internal class CustomEmoji constructor(
    raw: EmojiResponse<ICustomEmoji>,
    guildId: Snowflake
) : Emoji(raw), ICustomEmoji {
    override suspend fun edit(builder: EmojiEditBuilder.() -> Unit) = rest.call(guild.id.some(), Id.functor()) {
        emojiService.editGuildEmoji(guild.id, id, builder.build(::EmojiEditBuilder)).just()
    }.extract()

    override val guild = guildId.identify<IGuild> {
        client.getGuild(it)
    }

    override val roles = Stream.chunk(Chunk.iterable(raw.roles.orEmpty()))
        .effectMap { guild().getRole(it) }.filterNotNull()

    override val creator: IdentifiedF<ForId, IUser>? = raw.user?.id?.identifyId { raw.user.unwrap() }

    override val requireColons: Boolean = raw.require_colons

    override val isManaged: Boolean = raw.managed

    override val isAnimated: Boolean = raw.animated

    override val id: Snowflake = raw.id!!

    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:${id.asString()}>"

    override suspend fun delete(reason: String?) = rest.effect {
        emojiService.deleteGuildEmoji(guild.id, id)
    }

    override fun toString(): String {
        return "CustomEmoji(guild=$guild, roles=$roles, creator=$creator, requireColons=$requireColons, isManaged=$isManaged, isAnimated=$isAnimated, id=$id, mention='$mention') " +
                "\n   ${super.toString()}"
    }
}
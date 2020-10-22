package org.tesserakt.diskordin.impl.core.entity


import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.some
import arrow.fx.coroutines.stream.Chunk
import arrow.fx.coroutines.stream.Stream
import arrow.fx.coroutines.stream.filterOption
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
        return StringBuilder("Emoji(")
            .appendLine("name='$name'")
            .appendLine(")")
            .toString()
    }
}

internal class CustomEmoji constructor(
    raw: EmojiResponse<ICustomEmoji>,
    guildId: Snowflake
) : Emoji(raw), ICustomEmoji {
    override suspend fun edit(builder: EmojiEditBuilder.() -> Unit) = rest.call(guild.id.some(), Id.functor()) {
        emojiService.editGuildEmoji(guild.id, id, builder.build(::EmojiEditBuilder))
    }.extract()

    override val guild = guildId.identify<IGuild> {
        client.getGuild(it)
    }

    override val roles = Stream.chunk(Chunk.array(raw.roles.orEmpty()))
        .effectMap { guild().getRole(it) }
        .filterOption()

    override val creator: IdentifiedF<ForId, IUser>? = raw.user?.id?.identifyId { raw.user.unwrap() }

    override val requireColons: Boolean = raw.require_colons ?: false

    override val isManaged: Boolean = raw.managed ?: false

    override val isAnimated: Boolean = raw.animated ?: false

    override val id: Snowflake = raw.id!!

    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:${id.asString()}>"

    override suspend fun delete(reason: String?) = rest.effect {
        emojiService.deleteGuildEmoji(guild.id, id)
    }

    override fun toString(): String {
        return StringBuilder("CustomEmoji(")
            .appendLine("guild=$guild, ")
            .appendLine("roles=$roles, ")
            .appendLine("creator=$creator, ")
            .appendLine("requireColons=$requireColons, ")
            .appendLine("isManaged=$isManaged, ")
            .appendLine("isAnimated=$isAnimated, ")
            .appendLine("id=$id, ")
            .appendLine("mention='$mention'")
            .appendLine(") ${super.toString()}")
            .toString()
    }
}
package ru.tesserakt.diskordin.impl.core.entity


import arrow.core.Id
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.k
import arrow.core.some
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.monad.map
import arrow.fx.fix
import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.EmojiResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
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
    override fun edit(builder: EmojiEditBuilder.() -> Unit) = rest.call(guild.id.some(), Id.functor()) {
        emojiService.editGuildEmoji(guild.id, id, builder.build())
    }.map { it.extract() }

    override val guild = guildId identify {
        client.getGuild(it).bind()
    }

    override val roles = IO.fx {
        raw.roles!!.map { guild().bind().getRole(it).bind() }.k()
    }

    override val creator: Identified<IUser> = raw.user!!.id identify { raw.user.unwrap() }

    override val requireColons: Boolean = raw.require_colons ?: throw NotCustomEmojiException()

    override val isManaged: Boolean = raw.managed ?: throw NotCustomEmojiException()

    override val isAnimated: Boolean = raw.animated ?: throw NotCustomEmojiException()

    override val id: Snowflake = raw.id ?: throw NotCustomEmojiException()

    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:${id.asString()}>"

    override fun delete(reason: String?) = rest.effect {
        emojiService.deleteGuildEmoji(guild.id, id)
    }.fix()

    override fun toString(): String {
        return "CustomEmoji(guild=$guild, roles=$roles, creator=$creator, requireColons=$requireColons, isManaged=$isManaged, isAnimated=$isAnimated, id=$id, mention='$mention') ${super.toString()}"
    }
}

class NotCustomEmojiException : IllegalArgumentException("Not a custom emoji")
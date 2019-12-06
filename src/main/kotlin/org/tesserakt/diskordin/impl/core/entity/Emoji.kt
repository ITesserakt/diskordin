package org.tesserakt.diskordin.impl.core.entity


import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import arrow.core.k
import arrow.core.some
import arrow.fx.IO
import arrow.fx.extensions.fx
import arrow.fx.extensions.io.monad.map
import arrow.fx.fix
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build
import org.tesserakt.diskordin.rest.call

open class Emoji(raw: EmojiResponse<IEmoji>) : IEmoji {
    final override val name: String = raw.name

    override fun toString(): String {
        return StringBuilder("Emoji(")
            .appendln("name='$name'")
            .appendln(")")
            .toString()
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
        client.getGuild(it)
    }

    override val roles = IO.fx {
        raw.roles!!.map { guild().bind().getRole(it).bind() }.k()
    }

    override val creator: IdentifiedF<ForId, IUser> = raw.user!!.id identify { raw.user.unwrap().just() }

    override val requireColons: Boolean = raw.require_colons ?: throw NotCustomEmojiException()

    override val isManaged: Boolean = raw.managed ?: throw NotCustomEmojiException()

    override val isAnimated: Boolean = raw.animated ?: throw NotCustomEmojiException()

    override val id: Snowflake = raw.id ?: throw NotCustomEmojiException()

    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:${id.asString()}>"

    override fun delete(reason: String?) = rest.effect {
        emojiService.deleteGuildEmoji(guild.id, id)
    }.fix()

    override fun toString(): String {
        return StringBuilder("CustomEmoji(")
            .appendln("guild=$guild, ")
            .appendln("roles=$roles, ")
            .appendln("creator=$creator, ")
            .appendln("requireColons=$requireColons, ")
            .appendln("isManaged=$isManaged, ")
            .appendln("isAnimated=$isAnimated, ")
            .appendln("id=$id, ")
            .appendln("mention='$mention'")
            .appendln(") ${super.toString()}")
            .toString()
    }
}

class NotCustomEmojiException : IllegalArgumentException("Not a custom emoji")
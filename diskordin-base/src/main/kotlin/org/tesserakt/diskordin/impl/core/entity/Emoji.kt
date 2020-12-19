@file:Suppress("DEPRECATION")

package org.tesserakt.diskordin.impl.core.entity


import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.functor.functor
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
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
    override suspend fun edit(builder: EmojiEditBuilder.() -> Unit) =
        rest.call<ForId, ICustomEmoji, EmojiResponse<ICustomEmoji>>(guild.id, Id.functor()) {
            emojiService.editGuildEmoji(guild.id, id, builder.build(::EmojiEditBuilder)).just()
        }.extract()

    override val guild = guildId.identify<IGuild> {
        client.getGuild(it)
    }

    override val roles = raw.roles.orEmpty().asFlow()
        .mapNotNull { guild().getRole(it) }

    override val creator: IdentifiedF<ForId, IUser>? = raw.user?.id?.identifyId { raw.user.unwrap() }

    override val requireColons: Boolean = raw.require_colons

    override val isManaged: Boolean = raw.managed

    override val isAnimated: Boolean = raw.animated

    override val id: Snowflake = raw.id!!

    override val mention: String = "<${if (isAnimated) "a" else ""}:$name:${id}>"

    override suspend fun delete(reason: String?) = rest.effect {
        emojiService.deleteGuildEmoji(guild.id, id)
    }

    override fun toString(): String {
        return "CustomEmoji(guild=$guild, roles=$roles, creator=$creator, requireColons=$requireColons, isManaged=$isManaged, isAnimated=$isAnimated, id=$id, mention='$mention') " +
                "\n   ${super.toString()}"
    }
}
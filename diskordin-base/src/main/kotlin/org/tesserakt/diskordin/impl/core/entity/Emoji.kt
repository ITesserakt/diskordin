@file:Suppress("DEPRECATION")

package org.tesserakt.diskordin.impl.core.entity

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.deferred
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.json.response.EmojiResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.core.entity.builder.EmojiEditBuilder
import org.tesserakt.diskordin.core.entity.builder.build

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
        rest.callRaw { emojiService.editGuildEmoji(guild.id, id, builder.build(::EmojiEditBuilder)) }.unwrap(guild.id)

    override val guild = guildId deferred {
        client.getGuild(it)
    }

    override val roles = raw.roles.orEmpty().asFlow()
        .mapNotNull { guild().getRole(it) }

    override val creator: EagerIdentified<IUser>? = raw.user?.id?.eager { raw.user.unwrap() }

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
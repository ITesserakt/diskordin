package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.IdUser
import org.tesserakt.diskordin.impl.core.entity.MessageUser
import org.tesserakt.diskordin.impl.core.entity.Self

internal val UserUpdater = CacheUpdater<IUser> { builder, data ->
    val user = builder.getUser(data.id)
    builder.copy(
        users = builder.users + when {
            data is Self || user == null || (user::class == data::class) -> mapOf(data.id to data)
            user is Self && data is IdUser -> mapOf(data.id to user.copy {
                it.copy(
                    username = data.raw.username ?: it.username,
                    discriminator = data.raw.discriminator ?: it.discriminator,
                    avatar = data.raw.avatar ?: it.avatar,
                    bot = data.raw.bot ?: it.bot,
                    mfa_enabled = data.raw.mfaEnabled ?: it.mfa_enabled,
                    locale = data.raw.locale ?: it.locale,
                    verified = data.raw.verified ?: it.verified,
                    email = data.raw.email ?: it.email,
                    flags = data.raw.flags ?: it.flags,
                    premium_type = data.raw.premiumType ?: it.premium_type
                )
            })
            user is Self && data is MessageUser -> mapOf(data.id to user.copy {
                it.copy(
                    username = data.raw.username,
                    discriminator = data.raw.discriminator.toString(),
                    avatar = data.raw.avatar,
                    bot = data.raw.bot ?: it.bot
                )
            })
            user is IdUser && data is MessageUser -> mapOf(data.id to user.copy {
                it.copy(
                    username = data.raw.username,
                    discriminator = data.raw.discriminator.toString(),
                    avatar = data.raw.avatar,
                    bot = data.raw.bot ?: it.bot
                )
            })
            user is MessageUser && data is IdUser -> mapOf(data.id to user.copy {
                it.copy(
                    username = data.raw.username ?: it.username,
                    discriminator = data.raw.discriminator?.toShort() ?: it.discriminator,
                    avatar = data.raw.avatar ?: it.avatar,
                    bot = data.raw.bot ?: it.bot
                )
            })
            else -> emptyMap()
        }
    )
}

internal val UserDeleter = CacheDeleter<IUser> { builder, data ->
    builder.copy(users = builder.users - data.id)
}
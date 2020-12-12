package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.IdUser
import org.tesserakt.diskordin.impl.core.entity.MessageUser
import org.tesserakt.diskordin.impl.core.entity.Self

internal val UserUpdater = CacheUpdater<IUser> { builder, data ->
    val user = builder.getUser(data.id)
    builder.users[data.id] = when {
        data is Self || data is MessageUser || user == null || (user::class == data::class) -> data
        user is Self && data is IdUser -> user.copy {
            it.copy(
                username = data.raw.username ?: it.username,
                discriminator = data.raw.discriminator ?: it.discriminator,
                avatar = data.raw.avatar ?: it.avatar,
                bot = data.raw.bot ?: it.bot,
                mfa_enabled = data.raw.mfaEnabled ?: it.mfa_enabled,
                locale = data.raw.locale ?: it.locale,
                publicFlags = data.raw.publicFlags ?: it.publicFlags,
                premium_type = data.raw.premiumType ?: it.premium_type
            )
        }
        user is MessageUser && data is IdUser -> user.copy {
            it.copy(
                username = data.raw.username ?: it.username,
                discriminator = data.raw.discriminator ?: it.discriminator,
                avatar = data.raw.avatar ?: it.avatar,
                bot = data.raw.bot ?: it.bot,
                system = data.raw.system ?: it.system,
                mfa_enabled = data.raw.mfaEnabled ?: it.mfa_enabled,
                locale = data.raw.locale ?: it.locale,
                publicFlags = data.raw.publicFlags ?: it.publicFlags,
                premium_type = data.raw.premiumType ?: it.premium_type
            )
        }
        else -> error("")
    }
}

internal val UserDeleter = CacheDeleter<IUser> { builder, data ->
    builder.users -= data
}
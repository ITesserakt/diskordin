package org.tesserakt.diskordin.core.cache

import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.impl.core.entity.IdUser
import org.tesserakt.diskordin.impl.core.entity.MessageUser
import org.tesserakt.diskordin.impl.core.entity.Self

class UserUpdater : CacheUpdater<IUser> {
    override fun handle(builder: CacheSnapshotBuilder, data: IUser) {
        when (val user = builder.getUser(data.id)) {
            is Self -> when (data) {
                is Self -> builder.users += data.id to data
                is IdUser -> builder.users += data.id to user.copy {
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
                }
                is MessageUser -> builder.users += data.id to user.copy {
                    it.copy(
                        username = data.raw.username,
                        discriminator = data.raw.discriminator.toString(),
                        avatar = data.raw.avatar,
                        bot = data.raw.bot ?: it.bot
                    )
                }
            }
            is IdUser -> when (data) {
                is Self, is IdUser -> builder.users += data.id to data
                is MessageUser -> builder.users += data.id to user.copy {
                    it.copy(
                        username = data.raw.username,
                        discriminator = data.raw.discriminator.toString(),
                        avatar = data.raw.avatar,
                        bot = data.raw.bot ?: it.bot
                    )
                }
            }
            is MessageUser -> when (data) {
                is Self, is MessageUser -> builder.users += data.id to data
                is IdUser -> builder.users += data.id to user.copy {
                    it.copy(
                        username = data.raw.username ?: it.username,
                        discriminator = data.raw.discriminator?.toShort() ?: it.discriminator,
                        avatar = data.raw.avatar ?: it.avatar,
                        bot = data.raw.bot ?: it.bot
                    )
                }
            }
            null -> builder.users += data.id to data
        }
    }
}

class UserDeleter : CacheDeleter<IUser> {
    override fun handle(builder: CacheSnapshotBuilder, data: IUser) {
        builder.users -= data.id
    }
}
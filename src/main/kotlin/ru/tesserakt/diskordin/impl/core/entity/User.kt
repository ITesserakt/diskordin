package ru.tesserakt.diskordin.impl.core.entity

import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.IUser

class User(raw: UserResponse, override val kodein: Kodein) : IUser {
    override val username: String = raw.username

    override val discriminator: Short = raw.discriminator.toShort()

    override val isBot: Boolean = raw.bot ?: false

    override val id: Snowflake = raw.id.asSnowflake()

    override val client: IDiscordClient by instance()


    override val mention: String = "<@$id>"
}
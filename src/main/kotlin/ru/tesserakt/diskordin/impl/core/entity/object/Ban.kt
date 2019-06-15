package ru.tesserakt.diskordin.impl.core.entity.`object`

import kotlinx.coroutines.async
import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.BanResponse
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IBan
import ru.tesserakt.diskordin.impl.core.entity.User
import ru.tesserakt.diskordin.util.Identified

class Ban(raw: BanResponse, override val kodein: Kodein = Diskordin.kodein) : IBan {
    override val reason: String? = raw.reason

    override val user: Identified<IUser> = Identified(raw.user.id.asSnowflake()) {
        client.coroutineScope.async { User(raw.user) }
    }

    override val client: IDiscordClient by instance()
}
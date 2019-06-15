package ru.tesserakt.diskordin.impl.core.entity

import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.asSnowflake
import ru.tesserakt.diskordin.core.data.json.response.ConnectionResponse
import ru.tesserakt.diskordin.core.entity.IConnection

class Connection(raw: ConnectionResponse, override val kodein: Kodein = Diskordin.kodein) : IConnection {
    override val type: String = raw.type

    override val isRevoked: Boolean = raw.revoked

    override val integrations: Array<Pair<Snowflake, String>> =
        raw.integrations.map { it.id.asSnowflake() to it.name }.toTypedArray()

    override val isVerified: Boolean = raw.verified

    override val isFriendSyncing: Boolean = raw.friend_sync

    override val isShowingActivity: Boolean = raw.show_activity

    override val visibility: IConnection.Visibility = IConnection.Visibility.of(raw.visibility)

    override val id: Snowflake = raw.id.asSnowflake()

    override val client: IDiscordClient by instance()

    override val name: String = raw.name
}

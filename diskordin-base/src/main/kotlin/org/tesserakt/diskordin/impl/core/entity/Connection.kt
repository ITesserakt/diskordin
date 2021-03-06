package org.tesserakt.diskordin.impl.core.entity


import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.asSnowflake
import org.tesserakt.diskordin.core.data.json.response.ConnectionResponse
import org.tesserakt.diskordin.core.entity.IConnection

internal class Connection(raw: ConnectionResponse) : IConnection {
    override val type: String = raw.type

    override val isRevoked: Boolean = raw.revoked

    override val integrations: List<Pair<Snowflake, String>> = raw.integrations

    override val isVerified: Boolean = raw.verified

    override val isFriendSyncing: Boolean = raw.friend_sync

    override val isShowingActivity: Boolean = raw.show_activity

    override val visibility =
        IConnection.Visibility.values().first { it.ordinal == raw.visibility }

    override val id: Snowflake = raw.id.asSnowflake()

    override val name: String = raw.name

    override fun toString(): String {
        return "Connection(type='$type', isRevoked=$isRevoked, integrations=${integrations}, isVerified=$isVerified, isFriendSyncing=$isFriendSyncing, isShowingActivity=$isShowingActivity, visibility=$visibility, id=$id, name='$name')"
    }
}

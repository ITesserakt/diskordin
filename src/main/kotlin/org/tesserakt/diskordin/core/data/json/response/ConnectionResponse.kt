package org.tesserakt.diskordin.core.data.json.response

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IConnection
import org.tesserakt.diskordin.impl.core.entity.Connection

private typealias ServerIntegration = Pair<Snowflake, String>

data class ConnectionResponse(
    val id: String,
    val name: String,
    val type: String,
    val revoked: Boolean,
    val integrations: Array<ServerIntegration>,
    val verified: Boolean,
    val friend_sync: Boolean,
    val show_activity: Boolean,
    val visibility: Int
) : DiscordResponse<IConnection, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IConnection = Connection(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ConnectionResponse

        if (id != other.id) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (revoked != other.revoked) return false
        if (!integrations.contentEquals(other.integrations)) return false
        if (verified != other.verified) return false
        if (friend_sync != other.friend_sync) return false
        if (show_activity != other.show_activity) return false
        if (visibility != other.visibility) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + revoked.hashCode()
        result = 31 * result + integrations.contentHashCode()
        result = 31 * result + verified.hashCode()
        result = 31 * result + friend_sync.hashCode()
        result = 31 * result + show_activity.hashCode()
        result = 31 * result + visibility
        return result
    }
}

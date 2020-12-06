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
    val integrations: List<ServerIntegration>,
    val verified: Boolean,
    val friend_sync: Boolean,
    val show_activity: Boolean,
    val visibility: Int
) : DiscordResponse<IConnection, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IConnection = Connection(this)
}
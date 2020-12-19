package org.tesserakt.diskordin.core.data.json.response

import com.google.gson.annotations.SerializedName
import org.tesserakt.diskordin.core.entity.`object`.IGatewayStats
import org.tesserakt.diskordin.impl.core.entity.`object`.GatewayStats

data class GatewayBotResponse(
    val url: String,
    val shards: Int,
    @SerializedName("session_start_limit") val sessionMeta: SessionStartLimit
) : DiscordResponse<IGatewayStats, UnwrapContext.EmptyContext>() {
    override fun unwrap(ctx: UnwrapContext.EmptyContext): IGatewayStats = GatewayStats(this)

    data class SessionStartLimit(
        val total: Int,
        val remaining: Int,
        val resetAfter: Int
    ) : DiscordResponse<IGatewayStats.ISession, UnwrapContext.EmptyContext>() {
        override fun unwrap(ctx: UnwrapContext.EmptyContext): IGatewayStats.ISession = GatewayStats.Session(this)
    }
}
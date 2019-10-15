package ru.tesserakt.diskordin.core.data.json.response

import com.google.gson.annotations.SerializedName
import ru.tesserakt.diskordin.core.entity.`object`.IGatewayStats
import ru.tesserakt.diskordin.impl.core.entity.`object`.GatewayStats

data class GatewayBotResponse(
    val url: String,
    val shards: Int,
    @SerializedName("session_start_limit") val sessionMeta: SessionStartLimit
) : DiscordResponse<IGatewayStats>() {
    override fun unwrap(vararg params: Any): IGatewayStats = GatewayStats(this)

    data class SessionStartLimit(
        val total: Int,
        val remaining: Int,
        val resetAfter: Int
    ) : DiscordResponse<IGatewayStats.ISession>() {
        override fun unwrap(vararg params: Any): IGatewayStats.ISession = GatewayStats.Session(this)
    }
}
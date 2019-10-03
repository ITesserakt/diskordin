package ru.tesserakt.diskordin.core.data.json.response

import com.google.gson.annotations.SerializedName

data class GatewayBotResponse(
    val url: String,
    val shards: Int,
    @SerializedName("session_start_limit") val sessionMeta: SessionStartLimit
) : DiscordResponse() {
    data class SessionStartLimit(
        val total: Int,
        val remaining: Int,
        val resetAfter: Int
    ) : DiscordResponse()
}
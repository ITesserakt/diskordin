package org.tesserakt.diskordin.gateway.json.commands

import com.google.gson.annotations.SerializedName
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.UserStatusUpdateRequest
import org.tesserakt.diskordin.gateway.json.IPayload
import org.tesserakt.diskordin.gateway.json.IRawEvent

sealed class GatewayCommand : IPayload

data class UpdateVoiceState(
    val guildId: Snowflake,
    val channelId: Snowflake? = null,
    val selfMute: Boolean,
    val selfDeaf: Boolean
) : GatewayCommand()

data class RequestGuildMembers(
    val guildId: Snowflake,
    val query: String = "",
    val limit: Int,
    @SerializedName("presences") val requirePresence: Boolean? = null,
    val userIds: List<Snowflake> = emptyList()
) : GatewayCommand()

data class Resume(
    val token: String,
    val sessionId: String,
    val seq: Int?
) : GatewayCommand()

data class Identify(
    val token: String,
    val properties: ConnectionProperties,
    val compress: Boolean = false,
    val largeThreshold: Int = 50,
    val shard: List<Int> = emptyList(),
    val presence: UserStatusUpdateRequest? = null,
    val intents: Short? = null,
    val guildSubscriptions: Boolean = true
) : GatewayCommand() {
    data class ConnectionProperties(
        @SerializedName("\$os") val os: String,
        @SerializedName("\$browser") val browser: String,
        @SerializedName("\$device") val device: String
    )
}

data class InvalidSession(
    val value: Boolean
) : GatewayCommand()

data class Heartbeat(
    val value: Int?
) : IRawEvent, GatewayCommand()

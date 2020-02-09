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
    val userIds: Array<Snowflake> = emptyArray()
) : GatewayCommand() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RequestGuildMembers

        if (guildId != other.guildId) return false
        if (query != other.query) return false
        if (limit != other.limit) return false
        if (requirePresence != other.requirePresence) return false
        if (!userIds.contentEquals(other.userIds)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = guildId.hashCode()
        result = 31 * result + query.hashCode()
        result = 31 * result + limit
        result = 31 * result + (requirePresence?.hashCode() ?: 0)
        result = 31 * result + userIds.contentHashCode()
        return result
    }
}

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
    val shard: Array<Int> = emptyArray(),
    val presence: UserStatusUpdateRequest? = null,
    val guildSubscriptions: Boolean = true
) : GatewayCommand() {
    data class ConnectionProperties(
        @SerializedName("\$os") val os: String,
        @SerializedName("\$browser") val browser: String,
        @SerializedName("\$device") val device: String
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Identify) return false

        if (token != other.token) return false
        if (properties != other.properties) return false
        if (compress != other.compress) return false
        if (largeThreshold != other.largeThreshold) return false
        if (!shard.contentEquals(other.shard)) return false
        if (guildSubscriptions != other.guildSubscriptions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = token.hashCode()
        result = 31 * result + properties.hashCode()
        result = 31 * result + compress.hashCode()
        result = 31 * result + largeThreshold
        result = 31 * result + shard.contentHashCode()
        result = 31 * result + guildSubscriptions.hashCode()
        return result
    }
}

data class InvalidSession(
    val value: Boolean
) : GatewayCommand()

data class Heartbeat(
    val value: Int?
) : IRawEvent, GatewayCommand()

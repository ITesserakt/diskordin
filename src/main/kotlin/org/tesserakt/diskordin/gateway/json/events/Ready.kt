package org.tesserakt.diskordin.gateway.json.events

import com.google.gson.annotations.SerializedName
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.IPrivateChannel
import org.tesserakt.diskordin.core.entity.ISelf
import org.tesserakt.diskordin.gateway.json.IRawEvent

typealias UnavailableGuild = Pair<Snowflake, Boolean>

data class Ready(
    @SerializedName("v") val gatewayProtocolVersion: Int,
    val user: UserResponse<ISelf>,
    val privateChannels: Array<ChannelResponse<IPrivateChannel>>,
    val guilds: Array<UnavailableGuild>,
    val sessionId: String,
    val shard: Array<Int>?,
    @SerializedName("_trace") val trace: Array<String>
) : IRawEvent {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Ready) return false

        if (gatewayProtocolVersion != other.gatewayProtocolVersion) return false
        if (user != other.user) return false
        if (!privateChannels.contentEquals(other.privateChannels)) return false
        if (!guilds.contentEquals(other.guilds)) return false
        if (sessionId != other.sessionId) return false
        if (shard != null) {
            if (other.shard == null) return false
            if (!shard.contentEquals(other.shard)) return false
        } else if (other.shard != null) return false
        if (!trace.contentEquals(other.trace)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = gatewayProtocolVersion
        result = 31 * result + user.hashCode()
        result = 31 * result + privateChannels.contentHashCode()
        result = 31 * result + guilds.contentHashCode()
        result = 31 * result + sessionId.hashCode()
        result = 31 * result + (shard?.contentHashCode() ?: 0)
        result = 31 * result + trace.contentHashCode()
        return result
    }
}
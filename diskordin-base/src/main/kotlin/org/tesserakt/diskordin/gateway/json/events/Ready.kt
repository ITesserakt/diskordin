package org.tesserakt.diskordin.gateway.json.events

import com.google.gson.annotations.SerializedName
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.*
import org.tesserakt.diskordin.gateway.json.IRawEvent

data class UnavailableGuild(val id: Snowflake, val available: Boolean) : IPreviewed<IGuild> {
    override suspend fun extend(): IGuild = client.getGuild(id)
}

data class Ready(
    @SerializedName("v") val gatewayProtocolVersion: Int,
    val user: UserResponse<ISelf>,
    val privateChannels: List<ChannelResponse<IPrivateChannel>>,
    val guilds: List<UnavailableGuild>,
    val sessionId: String,
    val shard: List<Int>?,
    @SerializedName("_trace") val trace: List<String>
) : IRawEvent
package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.DMCreateRequest
import org.tesserakt.diskordin.core.data.json.request.GroupDMCreateRequest
import org.tesserakt.diskordin.core.data.json.request.UserEditRequest
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.ConnectionResponse
import org.tesserakt.diskordin.core.data.json.response.UserGuildResponse
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.IGroupPrivateChannel
import org.tesserakt.diskordin.core.entity.IPrivateChannel
import org.tesserakt.diskordin.core.entity.ISelf
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.query.Query
import org.tesserakt.diskordin.rest.integration.parameters

class UserServiceImpl(private val ktor: HttpClient, private val discordApiUrl: String) : UserService {
    override suspend fun getCurrentUser(): UserResponse<ISelf> = ktor.get("$discordApiUrl/api/v6/users/@me")

    override suspend fun getUser(id: Snowflake): UserResponse<IUser> = ktor.get("$discordApiUrl/api/v6/users/$id")

    override suspend fun editCurrentUser(request: UserEditRequest): UserResponse<ISelf> =
        ktor.patch("$discordApiUrl/api/v6/users/@me") {
            body = request
        }

    override suspend fun getCurrentUserGuilds(query: Query): ListK<UserGuildResponse> =
        ktor.get("$discordApiUrl/api/v6/users/@me/guilds") {
            parameters(query)
        }

    override suspend fun leaveGuild(id: Snowflake): Unit = ktor.delete("$discordApiUrl/api/v6/users/@me/guilds/$id")

    override suspend fun getUserDMs(): ListK<ChannelResponse<IPrivateChannel>> =
        ktor.get("$discordApiUrl/api/v6/users/@me/channels")

    override suspend fun joinToDM(request: DMCreateRequest): ChannelResponse<IPrivateChannel> =
        ktor.post("$discordApiUrl/api/v6/users/@me/channels") {
            body = request
        }

    override suspend fun joinToGroupDM(request: GroupDMCreateRequest): ChannelResponse<IGroupPrivateChannel> =
        ktor.post("$discordApiUrl/api/v6/users/@me/channels") {
            body = request
        }

    override suspend fun getCurrentUserConnections(): ListK<ConnectionResponse> =
        ktor.get("$discordApiUrl/api/v6/users/@me/connections")
}
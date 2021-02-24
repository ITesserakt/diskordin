@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service


import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.data.json.request.DMCreateRequest
import org.tesserakt.diskordin.core.data.json.request.UserEditRequest
import org.tesserakt.diskordin.core.data.json.response.ChannelResponse
import org.tesserakt.diskordin.core.data.json.response.ConnectionResponse
import org.tesserakt.diskordin.core.data.json.response.UserGuildResponse
import org.tesserakt.diskordin.core.data.json.response.UserResponse
import org.tesserakt.diskordin.core.entity.IPrivateChannel
import org.tesserakt.diskordin.core.entity.ISelf
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.query.Query

interface UserService {
    suspend fun getCurrentUser(): UserResponse<ISelf>

    suspend fun getUser(id: Snowflake): UserResponse<IUser>

    suspend fun editCurrentUser(request: UserEditRequest): UserResponse<ISelf>

    suspend fun getCurrentUserGuilds(query: Query): List<UserGuildResponse>

    suspend fun leaveGuild(id: Snowflake)

    suspend fun getUserDMs(): List<ChannelResponse<IPrivateChannel>>

    suspend fun joinToDM(request: DMCreateRequest): ChannelResponse<IPrivateChannel>

    suspend fun getCurrentUserConnections(): List<ConnectionResponse>
}
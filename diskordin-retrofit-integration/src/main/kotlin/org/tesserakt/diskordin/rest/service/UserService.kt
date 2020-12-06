package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
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
import retrofit2.http.*

interface UserServiceImpl : UserService {
    @GET("/api/v6/users/@me")
    override suspend fun getCurrentUser(): UserResponse<ISelf>

    @GET("/api/v6/users/{id}")
    override suspend fun getUser(@Path("id") id: Snowflake): UserResponse<IUser>

    @PATCH("/api/v6/users/@me")
    override suspend fun editCurrentUser(@Body request: UserEditRequest): UserResponse<ISelf>

    @GET("/api/v6/users/@me/guilds")
    override suspend fun getCurrentUserGuilds(@QueryMap query: Query): ListK<UserGuildResponse>

    @DELETE("/api/v6/users/@me/guilds/{id}")
    override suspend fun leaveGuild(@Path("id") id: Snowflake)

    @GET("/api/v6/users/@me/channels")
    override suspend fun getUserDMs(): ListK<ChannelResponse<IPrivateChannel>>

    @POST("/api/v6/users/@me/channels")
    override suspend fun joinToDM(@Body request: DMCreateRequest): ChannelResponse<IPrivateChannel>

    @GET("/api/v6/users/@me/connections")
    override suspend fun getCurrentUserConnections(): ListK<ConnectionResponse>
}
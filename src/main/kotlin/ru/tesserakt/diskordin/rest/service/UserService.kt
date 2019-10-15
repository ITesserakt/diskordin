@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.service

import retrofit2.http.*
import ru.tesserakt.diskordin.core.data.Snowflake
import ru.tesserakt.diskordin.core.data.json.request.DMCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.GroupDMCreateRequest
import ru.tesserakt.diskordin.core.data.json.request.UserEditRequest
import ru.tesserakt.diskordin.core.data.json.response.ChannelResponse
import ru.tesserakt.diskordin.core.data.json.response.ConnectionResponse
import ru.tesserakt.diskordin.core.data.json.response.UserGuildResponse
import ru.tesserakt.diskordin.core.data.json.response.UserResponse
import ru.tesserakt.diskordin.core.entity.IGroupPrivateChannel
import ru.tesserakt.diskordin.core.entity.IPrivateChannel
import ru.tesserakt.diskordin.core.entity.ISelf
import ru.tesserakt.diskordin.core.entity.query.Query

interface UserService {
    @GET("/api/v6/users/@me")
    suspend fun getCurrentUser(): UserResponse<ISelf>

    @GET("/api/v6/users/{id}")
    suspend fun getUser(@Path("id") id: Snowflake): UserResponse<*>

    @PATCH("/api/v6/users/@me")
    suspend fun editCurrentUser(@Body request: UserEditRequest): UserResponse<ISelf>

    @GET("/api/v6/users/@me/guilds")
    suspend fun getCurrentUserGuilds(@QueryMap query: Query): Array<UserGuildResponse>

    @DELETE("/api/v6/users/@me/guilds/{id}")
    suspend fun leaveGuild(@Path("id") id: Snowflake)

    @GET("/api/v6/users/@me/channels")
    suspend fun getUserDMs(): Array<ChannelResponse<IPrivateChannel>>

    @POST("/api/v6/users/@me/channels")
    suspend fun joinToDM(@Body request: DMCreateRequest): ChannelResponse<IPrivateChannel>

    @Deprecated(
        "GameBridge SDK is deprecated for now",
        ReplaceWith("UserService.joinToDM()", "ru.tesserakt.diskordin.rest.service.UserService")
    )
    @POST("/api/v6/users/@me/channels")
    suspend fun joinToGroupDM(@Body request: GroupDMCreateRequest): ChannelResponse<IGroupPrivateChannel>

    @GET("/api/v6/users/@me/connections")
    suspend fun getCurrentUserConnections(): Array<ConnectionResponse>
}
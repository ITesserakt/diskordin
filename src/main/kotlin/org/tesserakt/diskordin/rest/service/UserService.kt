@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
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
import retrofit2.http.*

interface UserService {
    @GET("/api/v6/users/@me")
    suspend fun getCurrentUser(): Id<UserResponse<ISelf>>

    @GET("/api/v6/users/{id}")
    suspend fun getUser(@Path("id") id: Snowflake): Id<UserResponse<IUser>>

    @PATCH("/api/v6/users/@me")
    suspend fun editCurrentUser(@Body request: UserEditRequest): Id<UserResponse<ISelf>>

    @GET("/api/v6/users/@me/guilds")
    suspend fun getCurrentUserGuilds(@QueryMap query: Query): ListK<UserGuildResponse>

    @DELETE("/api/v6/users/@me/guilds/{id}")
    suspend fun leaveGuild(@Path("id") id: Snowflake): Unit

    @GET("/api/v6/users/@me/channels")
    suspend fun getUserDMs(): ListK<ChannelResponse<IPrivateChannel>>

    @POST("/api/v6/users/@me/channels")
    suspend fun joinToDM(@Body request: DMCreateRequest): Id<ChannelResponse<IPrivateChannel>>

    @Deprecated(
        "GameBridge SDK is deprecated for now",
        ReplaceWith("UserService.joinToDM()", "org.tesserakt.diskordin.rest.service.UserService")
    )
    @POST("/api/v6/users/@me/channels")
    suspend fun joinToGroupDM(@Body request: GroupDMCreateRequest): Id<ChannelResponse<IGroupPrivateChannel>>

    @GET("/api/v6/users/@me/connections")
    suspend fun getCurrentUserConnections(): ListK<ConnectionResponse>
}
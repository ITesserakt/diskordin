@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallK
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
    fun getCurrentUser(): CallK<Id<UserResponse<ISelf>>>

    @GET("/api/v6/users/{id}")
    fun getUser(@Path("id") id: Snowflake): CallK<Id<UserResponse<IUser>>>

    @PATCH("/api/v6/users/@me")
    fun editCurrentUser(@Body request: UserEditRequest): CallK<Id<UserResponse<ISelf>>>

    @GET("/api/v6/users/@me/guilds")
    fun getCurrentUserGuilds(@QueryMap query: Query): CallK<ListK<UserGuildResponse>>

    @DELETE("/api/v6/users/@me/guilds/{id}")
    fun leaveGuild(@Path("id") id: Snowflake): CallK<Unit>

    @GET("/api/v6/users/@me/channels")
    fun getUserDMs(): CallK<ListK<ChannelResponse<IPrivateChannel>>>

    @POST("/api/v6/users/@me/channels")
    fun joinToDM(@Body request: DMCreateRequest): CallK<Id<ChannelResponse<IPrivateChannel>>>

    @Deprecated(
        "GameBridge SDK is deprecated for now",
        ReplaceWith("UserService.joinToDM()", "org.tesserakt.diskordin.rest.service.UserService")
    )
    @POST("/api/v6/users/@me/channels")
    fun joinToGroupDM(@Body request: GroupDMCreateRequest): CallK<Id<ChannelResponse<IGroupPrivateChannel>>>

    @GET("/api/v6/users/@me/connections")
    fun getCurrentUserConnections(): CallK<ListK<ConnectionResponse>>
}
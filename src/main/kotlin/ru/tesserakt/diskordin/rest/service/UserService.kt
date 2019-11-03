@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallK
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
    fun getCurrentUser(): CallK<Id<UserResponse<ISelf>>>

    @GET("/api/v6/users/{id}")
    fun getUser(@Path("id") id: Snowflake): CallK<Id<UserResponse<*>>>

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
        ReplaceWith("UserService.joinToDM()", "ru.tesserakt.diskordin.rest.service.UserService")
    )
    @POST("/api/v6/users/@me/channels")
    fun joinToGroupDM(@Body request: GroupDMCreateRequest): CallK<Id<ChannelResponse<IGroupPrivateChannel>>>

    @GET("/api/v6/users/@me/connections")
    fun getCurrentUserConnections(): CallK<ListK<ConnectionResponse>>
}
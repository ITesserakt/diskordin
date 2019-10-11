package ru.tesserakt.diskordin.rest.service

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import ru.tesserakt.diskordin.core.data.json.response.InviteResponse

interface InviteService {
    @GET("/api/v6/invites/{code}")
    suspend fun getInvite(@Path("code") code: String): InviteResponse

    @DELETE("/api/v6/invites/{code}")
    suspend fun deleteInvite(@Path("code") code: String)

    @POST("/api/v6/invites/{code}")
    suspend fun acceptInvite(@Path("code") code: String)
}
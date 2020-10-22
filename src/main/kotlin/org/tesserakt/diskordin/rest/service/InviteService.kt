package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InviteService {
    @GET("/api/v6/invites/{code}")
    suspend fun getInvite(@Path("code") code: String): Id<InviteResponse<IInvite>>

    @DELETE("/api/v6/invites/{code}")
    suspend fun deleteInvite(@Path("code") code: String): Unit

    @POST("/api/v6/invites/{code}")
    suspend fun acceptInvite(@Path("code") code: String): Unit
}
package org.tesserakt.diskordin.rest.service

import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface InviteServiceImpl : InviteService {
    @GET("/api/v6/invites/{code}")
    override suspend fun getInvite(@Path("code") code: String): InviteResponse<IInvite>

    @DELETE("/api/v6/invites/{code}")
    override suspend fun deleteInvite(@Path("code") code: String)
}
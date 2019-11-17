package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import arrow.integrations.retrofit.adapter.CallK
import org.tesserakt.diskordin.core.data.json.response.InviteResponse
import org.tesserakt.diskordin.core.entity.`object`.IInvite
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface InviteService {
    @GET("/api/v6/invites/{code}")
    fun getInvite(@Path("code") code: String): CallK<Id<InviteResponse<IInvite>>>

    @DELETE("/api/v6/invites/{code}")
    fun deleteInvite(@Path("code") code: String): CallK<Unit>

    @POST("/api/v6/invites/{code}")
    fun acceptInvite(@Path("code") code: String): CallK<Unit>
}
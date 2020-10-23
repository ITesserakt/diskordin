package org.tesserakt.diskordin.rest.service

import arrow.core.Id
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.TemplateResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TemplateService {
    @GET("/api/v6/guilds/templates/{code}")
    suspend fun getTemplate(@Path("code") templateCode: String): Id<TemplateResponse>

    @POST("/api/v6/guilds/templates/{code}")
    suspend fun createGuildFromTemplate(@Path("code") templateCode: String): Id<GuildResponse>
}
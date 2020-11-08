package org.tesserakt.diskordin.rest.service

import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.TemplateResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TemplateServiceImpl : TemplateService {
    @GET("/api/v6/guilds/templates/{code}")
    override suspend fun getTemplate(@Path("code") templateCode: String): TemplateResponse

    @POST("/api/v6/guilds/templates/{code}")
    override suspend fun createGuildFromTemplate(@Path("code") templateCode: String): GuildResponse
}
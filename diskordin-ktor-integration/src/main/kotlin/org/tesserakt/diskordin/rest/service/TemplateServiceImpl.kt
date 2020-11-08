package org.tesserakt.diskordin.rest.service

import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.TemplateResponse

class TemplateServiceImpl(private val ktor: HttpClient, private val discordApiUrl: String) : TemplateService {
    override suspend fun getTemplate(templateCode: String): TemplateResponse =
        ktor.get("$discordApiUrl/api/v6/guilds/templates/$templateCode")

    override suspend fun createGuildFromTemplate(templateCode: String): GuildResponse =
        ktor.post("$discordApiUrl/api/v6/guilds/templates/$templateCode")
}

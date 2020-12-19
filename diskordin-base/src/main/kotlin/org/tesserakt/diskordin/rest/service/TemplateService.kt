package org.tesserakt.diskordin.rest.service

import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.TemplateResponse

interface TemplateService {
    suspend fun getTemplate(templateCode: String): TemplateResponse

    suspend fun createGuildFromTemplate(templateCode: String): GuildResponse
}
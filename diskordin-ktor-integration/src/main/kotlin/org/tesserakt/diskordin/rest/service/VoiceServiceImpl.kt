package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import io.ktor.client.*
import io.ktor.client.request.*
import org.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse

class VoiceServiceImpl(private val ktor: HttpClient, private val discordApiUrl: String) : VoiceService {
    override suspend fun getVoiceRegions(): ListK<VoiceRegionResponse> = ktor.get("$discordApiUrl/api/v6/voice/regions")
}
package org.tesserakt.diskordin.rest.service


import org.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import retrofit2.http.GET

interface VoiceServiceImpl : VoiceService {
    @GET("/api/v6/voice/regions")
    override suspend fun getVoiceRegions(): List<VoiceRegionResponse>
}
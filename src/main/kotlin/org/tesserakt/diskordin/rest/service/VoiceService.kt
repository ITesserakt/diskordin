@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import org.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import retrofit2.http.GET

interface VoiceService {
    @GET("/api/v6/voice/regions")
    suspend fun getVoiceRegions(): ListK<VoiceRegionResponse>
}
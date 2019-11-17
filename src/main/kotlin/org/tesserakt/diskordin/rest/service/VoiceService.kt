@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import arrow.integrations.retrofit.adapter.CallK
import org.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import retrofit2.http.GET

interface VoiceService {
    @GET("/api/v6/voice/regions")
    fun getVoiceRegions(): CallK<ListK<VoiceRegionResponse>>
}
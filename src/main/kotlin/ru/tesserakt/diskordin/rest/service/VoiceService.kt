@file:Suppress("unused")

package ru.tesserakt.diskordin.rest.service

import arrow.integrations.retrofit.adapter.CallK
import retrofit2.http.GET
import ru.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse

interface VoiceService {
    @GET("/api/v6/voice/regions")
    fun getVoiceRegions(): CallK<Array<VoiceRegionResponse>>
}
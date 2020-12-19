@file:Suppress("unused")

package org.tesserakt.diskordin.rest.service

import arrow.core.ListK
import org.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse

interface VoiceService {
    suspend fun getVoiceRegions(): ListK<VoiceRegionResponse>
}
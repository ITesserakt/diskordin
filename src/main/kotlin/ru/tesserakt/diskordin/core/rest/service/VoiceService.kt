@file:Suppress("unused")

package ru.tesserakt.diskordin.core.rest.service

import ru.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import ru.tesserakt.diskordin.core.rest.Routes

internal object VoiceService {
    object General {
        suspend fun getVoiceRegions() =
            Routes.getVoiceRegions()
                .newRequest()
                .resolve<VoiceRegionResponse>()
    }
}
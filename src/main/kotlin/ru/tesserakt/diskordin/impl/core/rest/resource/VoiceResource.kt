@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.rest.resource

import ru.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import ru.tesserakt.diskordin.impl.core.rest.Routes

internal object VoiceResource {
    object General {
        suspend fun getVoiceRegions() =
            Routes.getVoiceRegions()
                .newRequest()
                .resolve<Array<VoiceRegionResponse>>()
    }
}
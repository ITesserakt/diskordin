@file:Suppress("unused")

package ru.tesserakt.diskordin.impl.core.service

import ru.tesserakt.diskordin.impl.core.entity.`object`.Region
import ru.tesserakt.diskordin.rest.resource.VoiceResource

internal object VoiceService {
    suspend fun getVoiceRegions() =
        VoiceResource.General.getVoiceRegions().map { Region(it) }
}
package ru.tesserakt.diskordin.impl.core.entity.`object`

import org.kodein.di.Kodein
import org.kodein.di.generic.instance
import ru.tesserakt.diskordin.Diskordin
import ru.tesserakt.diskordin.core.client.IDiscordClient
import ru.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import ru.tesserakt.diskordin.core.entity.`object`.IRegion

class Region(raw: VoiceRegionResponse, override val kodein: Kodein = Diskordin.kodein) : IRegion {
    override val id: String = raw.id

    override val isOptimal: Boolean = raw.optimal

    override val isVIP: Boolean = raw.vip

    override val isDeprecated: Boolean = raw.deprecated

    override val isCustom: Boolean = raw.custom

    override val client: IDiscordClient by instance()

    override val name: String = raw.name
}
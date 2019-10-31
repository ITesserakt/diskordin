package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import ru.tesserakt.diskordin.core.entity.`object`.IRegion

class Region(raw: VoiceRegionResponse) : IRegion {
    override val id: String = raw.id

    override val isOptimal: Boolean = raw.optimal

    override val isVIP: Boolean = raw.vip

    override val isDeprecated: Boolean = raw.deprecated

    override val isCustom: Boolean = raw.custom

    override val name: String = raw.name

    override fun toString(): String {
        return "Region(id='$id', isOptimal=$isOptimal, isVIP=$isVIP, isDeprecated=$isDeprecated, isCustom=$isCustom, name='$name')"
    }
}
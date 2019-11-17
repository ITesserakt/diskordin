package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import org.tesserakt.diskordin.core.entity.`object`.IRegion

class Region(raw: VoiceRegionResponse) : IRegion {
    override val id: String = raw.id

    override val isOptimal: Boolean = raw.optimal

    override val isVIP: Boolean = raw.vip

    override val isDeprecated: Boolean = raw.deprecated

    override val isCustom: Boolean = raw.custom

    override val name: String = raw.name

    override fun toString(): String {
        return StringBuilder("Region(")
            .appendln("id='$id', ")
            .appendln("isOptimal=$isOptimal, ")
            .appendln("isVIP=$isVIP, ")
            .appendln("isDeprecated=$isDeprecated, ")
            .appendln("isCustom=$isCustom, ")
            .appendln("name='$name'")
            .appendln(")")
            .toString()
    }
}
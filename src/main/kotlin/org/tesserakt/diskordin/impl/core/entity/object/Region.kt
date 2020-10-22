package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.json.response.VoiceRegionResponse
import org.tesserakt.diskordin.core.entity.`object`.IRegion

internal class Region(raw: VoiceRegionResponse) : IRegion {
    override val id: String = raw.id

    override val isOptimal: Boolean = raw.optimal

    override val isVIP: Boolean = raw.vip

    override val isDeprecated: Boolean = raw.deprecated

    override val isCustom: Boolean = raw.custom

    override val name: String = raw.name

    override fun toString(): String {
        return StringBuilder("Region(")
            .appendLine("id='$id', ")
            .appendLine("isOptimal=$isOptimal, ")
            .appendLine("isVIP=$isVIP, ")
            .appendLine("isDeprecated=$isDeprecated, ")
            .appendLine("isCustom=$isCustom, ")
            .appendLine("name='$name'")
            .appendLine(")")
            .toString()
    }
}
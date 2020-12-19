package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.json.request.GuildRoleEditRequest
import org.tesserakt.diskordin.core.entity.`object`.IPermissionOverwrite
import java.awt.Color

@RequestBuilder
@Suppress("NOTHING_TO_INLINE", "unused")
class RoleEditBuilder : AuditLogging<GuildRoleEditRequest>() {
    override fun create(): GuildRoleEditRequest = GuildRoleEditRequest(
        name, permissions?.computeCode(), color?.rgb?.and(0xFFFFFF), isHoisted, isMentionable
    )

    private var name: String? = null
    private var permissions: IPermissionOverwrite? = null
    private var color: Color? = null
    private var isHoisted: Boolean? = null
    private var isMentionable: Boolean? = null

    operator fun Name.unaryPlus() {
        name = this.v
    }

    operator fun IPermissionOverwrite.unaryPlus() {
        permissions = this
    }

    operator fun Color.unaryPlus() {
        color = this
    }

    operator fun Hoisted.unaryPlus() {
        isHoisted = this.v
    }

    operator fun Boolean.unaryPlus() {
        isMentionable = this
    }

    inline fun RoleEditBuilder.name(name: String) = Name(name)
    inline fun RoleEditBuilder.permission(overwrite: IPermissionOverwrite) = overwrite
    inline fun RoleEditBuilder.color(color: Color) = color
    inline fun RoleEditBuilder.hoisted(value: Boolean) = Hoisted(value)
    inline fun RoleEditBuilder.mentionable(value: Boolean) = value
}
@file:Suppress("NOTHING_TO_INLINE", "unused")

package org.tesserakt.diskordin.core.entity.builder

import org.tesserakt.diskordin.core.data.Permissions
import org.tesserakt.diskordin.core.data.json.request.GuildRoleCreateRequest
import java.awt.Color
import kotlin.properties.Delegates

@RequestBuilder
class RoleCreateBuilder(val name: String, val color: Color) : AuditLogging<GuildRoleCreateRequest>() {
    private var permissions: Long by Delegates.notNull()
    private var isHoisted: Boolean by Delegates.notNull()
    private var isMentionable: Boolean by Delegates.notNull()

    operator fun Permissions.unaryPlus() {
        permissions = this.code
    }

    operator fun Boolean.unaryPlus() {
        isHoisted = this
    }

    operator fun Mentioned.unaryPlus() {
        isMentionable = this.v
    }

    inline fun RoleCreateBuilder.permissions(value: Permissions) = value
    inline fun RoleCreateBuilder.hoisted() = true
    inline fun RoleCreateBuilder.mentioned() = Mentioned(true)

    override fun create(): GuildRoleCreateRequest = GuildRoleCreateRequest(
        name,
        permissions,
        color.rgb,
        isHoisted,
        isMentionable
    )
}

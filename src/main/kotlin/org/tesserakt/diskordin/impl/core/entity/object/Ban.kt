package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.Identified
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.BanResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IBan

class Ban(raw: BanResponse) : IBan {
    override val reason: String? = raw.reason

    override val user: Identified<IUser> =
        raw.user.id identify { raw.user.unwrap() }

    override fun toString(): String {
        return StringBuilder("Ban(")
            .appendln("reason=$reason, ")
            .appendln("user=$user")
            .appendln(")")
            .toString()
    }
}
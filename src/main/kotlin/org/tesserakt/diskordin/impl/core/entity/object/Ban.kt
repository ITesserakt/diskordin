package org.tesserakt.diskordin.impl.core.entity.`object`


import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.BanResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IBan

internal class Ban(raw: BanResponse) : IBan {
    override val reason: String? = raw.reason

    override val user: IdentifiedF<ForId, IUser> =
        raw.user.id identify { raw.user.unwrap().just() }

    override fun toString(): String {
        return "Ban(reason=$reason, user=$user)"
    }
}
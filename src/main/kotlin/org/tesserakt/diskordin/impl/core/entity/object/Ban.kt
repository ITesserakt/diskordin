package org.tesserakt.diskordin.impl.core.entity.`object`


import arrow.core.ForId
import org.tesserakt.diskordin.core.data.IdentifiedF
import org.tesserakt.diskordin.core.data.identifyId
import org.tesserakt.diskordin.core.data.json.response.BanResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.ICacheable
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IBan

internal class Ban(override val raw: BanResponse) : IBan, ICacheable<IBan, UnwrapContext.EmptyContext, BanResponse> {
    override val reason: String? = raw.reason

    override val user: IdentifiedF<ForId, IUser> =
        raw.user.id identifyId { raw.user.unwrap() }

    override fun toString(): String {
        return "Ban(reason=$reason, user=$user)"
    }

    override fun fromCache(): IBan = TODO()

    override fun copy(changes: (BanResponse) -> BanResponse): IBan = raw.run(changes).unwrap()
}
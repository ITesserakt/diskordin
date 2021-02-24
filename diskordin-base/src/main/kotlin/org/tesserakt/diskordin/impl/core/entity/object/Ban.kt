package org.tesserakt.diskordin.impl.core.entity.`object`


import org.tesserakt.diskordin.core.data.EagerIdentified
import org.tesserakt.diskordin.core.data.eager
import org.tesserakt.diskordin.core.data.json.response.BanResponse
import org.tesserakt.diskordin.core.data.json.response.UnwrapContext
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.ICacheable
import org.tesserakt.diskordin.core.entity.IUser
import org.tesserakt.diskordin.core.entity.`object`.IBan

internal class Ban(override val raw: BanResponse) : IBan, ICacheable<IBan, UnwrapContext.EmptyContext, BanResponse> {
    override val reason: String? = raw.reason

    override val user: EagerIdentified<IUser> =
        raw.user.id eager { raw.user.unwrap() }

    override fun toString(): String {
        return "Ban(reason=$reason, user=$user)"
    }

    override fun copy(changes: (BanResponse) -> BanResponse): IBan = raw.run(changes).unwrap()
}
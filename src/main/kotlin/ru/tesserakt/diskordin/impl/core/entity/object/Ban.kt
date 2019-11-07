package ru.tesserakt.diskordin.impl.core.entity.`object`


import ru.tesserakt.diskordin.core.data.Identified
import ru.tesserakt.diskordin.core.data.identify
import ru.tesserakt.diskordin.core.data.json.response.BanResponse
import ru.tesserakt.diskordin.core.data.json.response.unwrap
import ru.tesserakt.diskordin.core.entity.IUser
import ru.tesserakt.diskordin.core.entity.`object`.IBan

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
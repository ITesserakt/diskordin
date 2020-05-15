package org.tesserakt.diskordin.core.data.event.guild

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import org.tesserakt.diskordin.core.data.id
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.invoke
import org.tesserakt.diskordin.core.data.json.response.GuildResponse
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache

class GuildUpdateEvent(raw: GuildResponse) : IGuildEvent<ForId> {
    override val guild = raw.id identify { raw.unwrap().just() }

    init {
        cache[guild.id] = guild().extract()
    }
}

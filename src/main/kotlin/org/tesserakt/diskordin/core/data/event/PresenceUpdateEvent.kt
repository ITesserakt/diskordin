@file:Suppress("unused")

package org.tesserakt.diskordin.core.data.event

import arrow.core.ForId
import arrow.core.extensions.id.applicative.just
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.list.traverse.sequence
import arrow.core.extensions.listk.monadFilter.filterMap
import arrow.core.identity
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.extensions.io.applicative.map
import org.tesserakt.diskordin.core.data.event.guild.IGuildEvent
import org.tesserakt.diskordin.core.data.identify
import org.tesserakt.diskordin.core.data.json.response.unwrap
import org.tesserakt.diskordin.core.entity.cache
import org.tesserakt.diskordin.core.entity.client
import org.tesserakt.diskordin.gateway.json.events.PresenceUpdate
import kotlin.time.ExperimentalTime

class PresenceUpdateEvent(raw: PresenceUpdate) : IGuildEvent<ForIO>, IUserEvent<ForId> {
    override val guild = raw.guildId identify { client.getGuild(it) }
    val roles = raw.roles.map { id ->
        guild().map { it.getRole(id) }
    }.sequence(IO.applicative()).map { it.filterMap(::identity) }
    @ExperimentalTime
    val game = raw.game?.unwrap()
    val status = UserStatus.valueOf(raw.status.toUpperCase())
    @ExperimentalTime
    val activities = raw.activities.map { it.unwrap() }
    val clientStatus = when {
        raw.clientStatus.desktop != null ->
            ClientStatus.Desktop(UserStatus.valueOf(raw.clientStatus.desktop.toUpperCase()))
        raw.clientStatus.mobile != null ->
            ClientStatus.Mobile(UserStatus.valueOf(raw.clientStatus.mobile.toUpperCase()))
        raw.clientStatus.web != null ->
            ClientStatus.Web(UserStatus.valueOf(raw.clientStatus.web.toUpperCase()))
        else -> null
    }
    override val user = raw.user.id identify { raw.user.unwrap().just() }

    init {
        cache.putIfAbsent(user.id, user().extract())
    }
}
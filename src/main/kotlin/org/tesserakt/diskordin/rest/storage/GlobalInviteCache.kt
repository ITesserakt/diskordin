package org.tesserakt.diskordin.rest.storage

import org.tesserakt.diskordin.core.entity.`object`.IInvite

object GlobalInviteCache : MutableMap<String, IInvite> by mutableMapOf()
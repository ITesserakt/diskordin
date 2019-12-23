package org.tesserakt.diskordin.rest.storage

import org.tesserakt.diskordin.core.entity.`object`.IInvite
import java.util.concurrent.ConcurrentHashMap

object GlobalInviteCache : MutableMap<String, IInvite> by ConcurrentHashMap()
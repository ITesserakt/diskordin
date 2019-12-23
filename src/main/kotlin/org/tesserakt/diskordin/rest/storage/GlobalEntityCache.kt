package org.tesserakt.diskordin.rest.storage

import org.tesserakt.diskordin.core.data.Snowflake
import org.tesserakt.diskordin.core.entity.IEntity
import java.util.concurrent.ConcurrentHashMap

object GlobalEntityCache : MutableMap<Snowflake, IEntity> by ConcurrentHashMap()